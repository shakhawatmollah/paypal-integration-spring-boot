package com.shakhawat.paypalintegration.service.impl;

import com.shakhawat.paypalintegration.dto.PaymentOrderDto;
import com.shakhawat.paypalintegration.dto.PaymentResponse;
import com.shakhawat.paypalintegration.exception.PaymentException;
import com.shakhawat.paypalintegration.model.Payment;
import com.shakhawat.paypalintegration.model.PaymentStatus;
import com.shakhawat.paypalintegration.repository.PaymentRepository;
import com.shakhawat.paypalintegration.service.PayPalService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class PayPalServiceImpl implements PayPalService {

    private final APIContext apiContext;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentOrderDto paymentOrderDto) {
        try {
            Amount amount = new Amount();
            amount.setCurrency(paymentOrderDto.getCurrency());
            amount.setTotal(String.format("%.2f", paymentOrderDto.getTotal()));

            Transaction transaction = new Transaction();
            transaction.setDescription(paymentOrderDto.getDescription());
            transaction.setAmount(amount);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod(paymentOrderDto.getMethod());

            com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
            payment.setIntent(paymentOrderDto.getIntent());
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl(paymentOrderDto.getCancelUrl());
            redirectUrls.setReturnUrl(paymentOrderDto.getSuccessUrl());
            payment.setRedirectUrls(redirectUrls);

            com.paypal.api.payments.Payment createdPayment = payment.create(apiContext);

            com.shakhawat.paypalintegration.model.Payment paymentEntity = mapToPaymentEntity(createdPayment, paymentOrderDto);
            paymentEntity.setPayerEmail(paymentOrderDto.getPayerEmail());
            String uniqueId = new IdGeneratorService().generateId();
            paymentEntity.setId(uniqueId);
            paymentRepository.save(paymentEntity);

            return buildPaymentResponse(createdPayment);
        } catch (PayPalRESTException e) {
            log.error("Error creating PayPal payment", e);
            throw new PaymentException("Error creating PayPal payment: " + e.getMessage(),
                    null, "PAYPAL_CREATION_ERROR", e);
        }
    }

    @Override
    @Transactional
    public Payment executePayment(String paymentId, String payerId) {
        try {
            com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            com.paypal.api.payments.Payment executedPayment = payment.execute(apiContext, paymentExecution);

            updatePaymentEntity(executedPayment);

            return paymentRepository.findByPaymentId(paymentId)
                    .orElseThrow(() -> new PaymentException("Payment not found",
                            paymentId, "PAYMENT_NOT_FOUND"));
        } catch (PayPalRESTException e) {
            log.error("Error executing PayPal payment", e);
            throw new PaymentException("Error executing PayPal payment: " + e.getMessage(),
                    paymentId, "PAYPAL_EXECUTION_ERROR", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentDetails(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found",
                        paymentId, "PAYMENT_NOT_FOUND"));
    }

    @Override
    @Transactional
    public void processWebhookEvent(Event event) {
        try {
            String eventType = event.getEventType();
            String resourceType = event.getResourceType();

            if ("PAYMENT.SALE.COMPLETED".equals(eventType) && "sale".equals(resourceType)) {
                handlePaymentCompletedEvent(event);
            } else if ("PAYMENT.CAPTURE.REFUNDED".equals(eventType)) {
                handleRefundEvent(event);
            }
            // Add other event types as needed
        } catch (Exception e) {
            log.error("Error processing webhook event", e);
            throw new PaymentException("Error processing webhook event",
                    null, "WEBHOOK_PROCESSING_ERROR", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPaymentInStatus(String paymentId, PaymentStatus status) {
        return paymentRepository.existsByPaymentIdAndStatus(paymentId, status);
    }

    @Override
    @Transactional
    public Payment refundPayment(String paymentId, BigDecimal amount, String note) {
        try {
            // Get the original payment
            com.paypal.api.payments.Payment paypalPayment =
                    com.paypal.api.payments.Payment.get(apiContext, paymentId);

            // Find sale ID to refund
//            String saleId = paypalPayment.getTransactions().get(0)
//                    .getRelatedResources().get(0)
//                    .getSale().getId();

            // Create refund
            RefundRequest refundRequest = new RefundRequest();
            Amount refundAmount = new Amount();
            refundAmount.setCurrency(paypalPayment.getTransactions().getFirst()
                    .getAmount().getCurrency());
            refundAmount.setTotal(amount != null ?
                    String.format("%.2f", amount) :
                    paypalPayment.getTransactions().getFirst().getAmount().getTotal());
            refundRequest.setAmount(refundAmount);
            refundRequest.setReason(note);

            // Execute refund
            DetailedRefund refund = new Sale().refund(apiContext, refundRequest);

            // Update payment status
            Payment payment = paymentRepository.findByPaymentId(paymentId)
                    .orElseThrow(() -> new PaymentException("Payment not found",
                            paymentId, "PAYMENT_NOT_FOUND"));

            payment.setStatus(amount != null &&
                    amount.compareTo(payment.getAmount()) < 0 ?
                    PaymentStatus.PARTIALLY_REFUNDED :
                    PaymentStatus.REFUNDED);

            paymentRepository.save(payment);

            return payment;
        } catch (PayPalRESTException e) {
            log.error("Error refunding payment", e);
            throw new PaymentException("Error refunding payment",
                    paymentId, "REFUND_ERROR", e);
        }
    }

    // Helper methods
    private Payment mapToPaymentEntity(com.paypal.api.payments.Payment paypalPayment,
                                       PaymentOrderDto dto) {
        Payment payment = new Payment();
        payment.setPaymentId(paypalPayment.getId());
        payment.setIntent(paypalPayment.getIntent());
        payment.setState(paypalPayment.getState());
        payment.setAmount(dto.getTotal());
        payment.setCurrency(dto.getCurrency());
        payment.setDescription(dto.getDescription());
        payment.setStatus(PaymentStatus.valueOf(paypalPayment.getState().toUpperCase()));
        return payment;
    }

    private void updatePaymentEntity(com.paypal.api.payments.Payment paypalPayment) {
        paymentRepository.findByPaymentId(paypalPayment.getId())
                .ifPresent(p -> {
                    p.setState(paypalPayment.getState());
                    p.setStatus(PaymentStatus.valueOf(paypalPayment.getState().toUpperCase()));
                    if (paypalPayment.getPayer() != null &&
                            paypalPayment.getPayer().getPayerInfo() != null) {
                        p.setPayerEmail(paypalPayment.getPayer().getPayerInfo().getEmail());
                    }
                    paymentRepository.save(p);
                });
    }

    private PaymentResponse buildPaymentResponse(com.paypal.api.payments.Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setStatus(payment.getState());
        response.setLinks(payment.getLinks());

        payment.getLinks().stream()
                .filter(link -> link.getRel().equals("approval_url"))
                .findFirst()
                .ifPresent(link -> response.setApprovalUrl(link.getHref()));

        return response;
    }

    private void handlePaymentCompletedEvent(Event event) {
        Sale sale = (Sale) event.getResource(); // Cast resource to Sale
        String paymentId = sale.getParentPayment(); // Retrieve parent payment ID
        paymentRepository.findByPaymentId(paymentId)
                .ifPresent(p -> {
                    p.setStatus(PaymentStatus.COMPLETED);
                    paymentRepository.save(p);
                });
    }

    private void handleRefundEvent(Event event) {
        Sale sale = (Sale) event.getResource(); // Cast resource to Sale
        String paymentId = sale.getParentPayment(); // Retrieve parent payment ID
        paymentRepository.findByPaymentId(paymentId)
                .ifPresent(p -> {
                    p.setStatus(PaymentStatus.REFUNDED);
                    paymentRepository.save(p);
                });
    }

    public record IdGeneratorService() {
        public String generateId() {
            return UUID.randomUUID().toString();
        }
    }
}
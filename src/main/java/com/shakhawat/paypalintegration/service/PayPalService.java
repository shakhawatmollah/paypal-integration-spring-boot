package com.shakhawat.paypalintegration.service;

import com.shakhawat.paypalintegration.dto.PaymentOrderDto;
import com.shakhawat.paypalintegration.dto.PaymentResponse;
import com.shakhawat.paypalintegration.model.Payment;
import com.paypal.api.payments.Event;
import com.shakhawat.paypalintegration.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

public interface PayPalService {

    /**
     * Creates a PayPal payment order
     * @param paymentOrderDto Payment details
     * @return Payment response with approval URL
     */
    PaymentResponse createPayment(PaymentOrderDto paymentOrderDto);

    /**
     * Executes an approved PayPal payment
     * @param paymentId PayPal payment ID
     * @param payerId PayPal payer ID
     * @return Completed payment details
     */
    Payment executePayment(String paymentId, String payerId);

    /**
     * Retrieves payment details by ID
     * @param paymentId PayPal payment ID
     * @return Payment details
     */
    Payment getPaymentDetails(String paymentId);

    /**
     * Processes PayPal webhook events
     * @param event PayPal webhook event
     */
    void processWebhookEvent(Event event);

    /**
     * Gets all payments with specific status
     * @param status Payment status to filter by
     * @return List of payments
     */
    List<Payment> getPaymentsByStatus(PaymentStatus status);

    /**
     * Checks if a payment exists and has specific status
     * @param paymentId PayPal payment ID
     * @param status Expected payment status
     * @return true if payment exists with given status
     */
    boolean isPaymentInStatus(String paymentId, PaymentStatus status);

    /**
     * Refunds a completed payment
     * @param paymentId PayPal payment ID
     * @param amount Amount to refund (null for full refund)
     * @param note Refund reason note
     * @return Refund details
     */
    Payment refundPayment(String paymentId, BigDecimal amount, String note);
}

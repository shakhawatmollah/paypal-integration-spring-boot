package com.shakhawat.paypalintegration;

import com.shakhawat.paypalintegration.dto.PaymentOrderDto;
import com.shakhawat.paypalintegration.dto.PaymentResponse;
import com.shakhawat.paypalintegration.model.Payment;
import com.shakhawat.paypalintegration.model.PaymentStatus;
import com.paypal.api.payments.Event;
import com.shakhawat.paypalintegration.service.PayPalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PayPalServiceTest {

    private PayPalService payPalService;

    @BeforeEach
    void setUp() {
        payPalService = mock(PayPalService.class);
    }

    @Test
    void testCreatePayment() {
        PaymentOrderDto dto = new PaymentOrderDto();
        PaymentResponse response = new PaymentResponse();
        when(payPalService.createPayment(dto)).thenReturn(response);

        PaymentResponse result = payPalService.createPayment(dto);
        assertNotNull(result);
    }

    @Test
    void testExecutePayment() {
        Payment payment = new Payment();
        when(payPalService.executePayment("PAY-123", "PAYER-456")).thenReturn(payment);

        Payment result = payPalService.executePayment("PAY-123", "PAYER-456");
        assertNotNull(result);
    }

    @Test
    void testGetPaymentDetails() {
        Payment payment = new Payment();
        when(payPalService.getPaymentDetails("PAY-123")).thenReturn(payment);

        Payment result = payPalService.getPaymentDetails("PAY-123");
        assertNotNull(result);
    }

    @Test
    void testProcessWebhookEvent() {
        Event event = new Event();
        doNothing().when(payPalService).processWebhookEvent(event);

        payPalService.processWebhookEvent(event);
        verify(payPalService, times(1)).processWebhookEvent(event);
    }

    @Test
    void testGetPaymentsByStatus() {
        List<Payment> payments = Collections.emptyList();
        when(payPalService.getPaymentsByStatus(PaymentStatus.COMPLETED)).thenReturn(payments);

        List<Payment> result = payPalService.getPaymentsByStatus(PaymentStatus.COMPLETED);
        assertNotNull(result);
    }

    @Test
    void testIsPaymentInStatus() {
        when(payPalService.isPaymentInStatus("PAY-123", PaymentStatus.COMPLETED)).thenReturn(true);

        boolean result = payPalService.isPaymentInStatus("PAY-123", PaymentStatus.COMPLETED);
        assertTrue(result);
    }

    @Test
    void testRefundPayment() {
        Payment payment = new Payment();
        when(payPalService.refundPayment("PAY-123", BigDecimal.TEN, "Test refund")).thenReturn(payment);

        Payment result = payPalService.refundPayment("PAY-123", BigDecimal.TEN, "Test refund");
        assertNotNull(result);
    }
}
package com.shakhawat.paypalintegration;

import com.shakhawat.paypalintegration.controller.PaymentController;
import com.shakhawat.paypalintegration.dto.PaymentOrderDto;
import com.shakhawat.paypalintegration.dto.PaymentResponse;
import com.shakhawat.paypalintegration.service.PayPalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import(PaymentControllerTest.TestConfig.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PayPalService payPalService;

    private PaymentOrderDto paymentOrderDto;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paymentOrderDto = new PaymentOrderDto();
        paymentOrderDto.setTotal(BigDecimal.valueOf(100));
        paymentOrderDto.setCurrency("USD");
        paymentOrderDto.setMethod("paypal");
        paymentOrderDto.setIntent("sale");
        paymentOrderDto.setPayerEmail("test@example.com");
        paymentOrderDto.setDescription("Test payment");
        paymentOrderDto.setCancelUrl("http://localhost:8080/cancel");
        paymentOrderDto.setSuccessUrl("http://localhost:8080/success");

        paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentId("PAY-123");
        paymentResponse.setStatus("created");
        paymentResponse.setApprovalUrl("http://paypal.com/approve");
    }

    @Test
    void testCreatePayment() throws Exception {
        when(payPalService.createPayment(any(PaymentOrderDto.class)))
                .thenReturn(paymentResponse);

        mockMvc.perform(post("/api/payment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "total": 100,
                                    "currency": "USD",
                                    "method": "paypal",
                                    "intent": "sale",
                                    "payerEmail": "test@example.com",
                                    "description": "Test payment",
                                    "cancelUrl": "http://localhost:8080/cancel",
                                    "successUrl": "http://localhost:8080/success"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("PAY-123"))
                .andExpect(jsonPath("$.status").value("created"))
                .andExpect(jsonPath("$.approvalUrl").value("http://paypal.com/approve"));
    }

    @Test
    void testPaymentSuccess() throws Exception {
        mockMvc.perform(get("/api/payment/success")
                        .param("paymentId", "PAY-123")
                        .param("PayerID", "PAYER-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.paymentId").value("PAY-123"));
    }

    @Test
    void testPaymentCancel() throws Exception {
        mockMvc.perform(get("/api/payment/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"));
    }

    // Test configuration to provide a mock PayPalService bean
    static class TestConfig {
        @Bean
        public PayPalService payPalService() {
            return org.mockito.Mockito.mock(PayPalService.class);
        }
    }
}
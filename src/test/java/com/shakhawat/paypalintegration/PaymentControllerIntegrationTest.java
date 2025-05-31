package com.shakhawat.paypalintegration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shakhawat.paypalintegration.dto.PaymentOrderDto;
import com.shakhawat.paypalintegration.dto.PaymentResponse;
import com.shakhawat.paypalintegration.model.Payment;
import com.shakhawat.paypalintegration.model.PaymentStatus;
import com.shakhawat.paypalintegration.service.PayPalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PayPalService payPalService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        Mockito.reset(payPalService);
    }

    @Test
    void integrationTestCreatePayment() throws Exception {
        // Arrange input DTO
        PaymentOrderDto dto = new PaymentOrderDto();
        dto.setTotal(BigDecimal.valueOf(100));
        dto.setCurrency("USD");
        dto.setMethod("paypal");
        dto.setIntent("sale");
        dto.setPayerEmail("shakhawat@example.com");
        dto.setDescription("Test payment");
        dto.setCancelUrl("http://localhost:8080/cancel");
        dto.setSuccessUrl("http://localhost:8080/success");

        // Prepare mock response
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId("PAY-123");
        response.setStatus("created");
        response.setApprovalUrl("http://paypal.com/approve");
        response.setLinks(Collections.emptyList()); // ensure not null

        // Stub the mock
        when(payPalService.createPayment(any(PaymentOrderDto.class))).thenReturn(response);

        // Act + Assert + Print actual response
        mockMvc.perform(post("/api/payment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()) // <-- Print response body
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("PAY-123"))) // Safety check
                .andExpect(jsonPath("$.paymentId").value("PAY-123"))
                .andExpect(jsonPath("$.status").value("created"))
                .andExpect(jsonPath("$.approvalUrl").value("http://paypal.com/approve"));
    }


    @Test
    void integrationTestPaymentSuccess() throws Exception {
        Payment response = new Payment();
        response.setPaymentId("PAY-123");
        response.setStatus(PaymentStatus.COMPLETED);

        when(payPalService.executePayment("PAY-123", "PAYER-456")).thenReturn(response);

        mockMvc.perform(get("/api/payment/success")
                        .param("paymentId", "PAY-123")
                        .param("PayerID", "PAYER-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.paymentId").value("PAY-123"));
    }

    @Test
    void integrationTestPaymentCancel() throws Exception {
        mockMvc.perform(get("/api/payment/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"));
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public PayPalService payPalService() {
            return Mockito.mock(PayPalService.class);
        }
    }
}

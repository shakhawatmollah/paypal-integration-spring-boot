package com.shakhawat.paypalintegration.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentOrderDto {
    private BigDecimal total;
    private String currency;
    private String method;
    private String intent;
    private String payerEmail;
    private String description;
    private String cancelUrl;
    private String successUrl;
}
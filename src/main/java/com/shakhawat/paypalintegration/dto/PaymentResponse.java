package com.shakhawat.paypalintegration.dto;

import com.paypal.api.payments.Links;
import lombok.Data;

import java.util.List;

@Data
public class PaymentResponse {
    private String paymentId;
    private String status;
    private String approvalUrl;
    private List<Links> links;
}

package com.shakhawat.paypalintegration.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaymentResponse {
    private String paymentId;
    private String status;
    private String approvalUrl;
    private List<LinkDto> links;
}

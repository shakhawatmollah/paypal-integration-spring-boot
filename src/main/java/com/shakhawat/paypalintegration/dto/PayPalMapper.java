package com.shakhawat.paypalintegration.dto;

import com.paypal.api.payments.Links;

import java.util.List;
import java.util.stream.Collectors;

public class PayPalMapper {

    public static List<LinkDto> toLinkDtoList(List<Links> links) {
        if (links == null) return null;

        return links.stream().map(link -> {
            LinkDto dto = new LinkDto();
            dto.setHref(link.getHref());
            dto.setRel(link.getRel());
            dto.setMethod(link.getMethod());
            return dto;
        }).collect(Collectors.toList());
    }

    public static PaymentResponse toPaymentResponse(String paymentId, String status, String approvalUrl, List<Links> links) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(paymentId);
        response.setStatus(status);
        response.setApprovalUrl(approvalUrl);
        response.setLinks(toLinkDtoList(links));
        return response;
    }
}


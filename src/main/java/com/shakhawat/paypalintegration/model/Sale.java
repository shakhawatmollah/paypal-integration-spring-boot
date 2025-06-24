package com.shakhawat.paypalintegration.model;

import lombok.Data;

@Data
public class Sale {
    private String id;
    private String state;
    private Amount amount;
    private String payment_mode;
    private String protection_eligibility;
    private String create_time;
    private String update_time;
    private String parent_payment;

    @Data
    public static class Amount {
        private String total;
        private String currency;
    }
}


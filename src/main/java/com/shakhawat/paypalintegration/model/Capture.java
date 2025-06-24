package com.shakhawat.paypalintegration.model;

import lombok.Data;

@Data
public class Capture {
    private String id;
    private Amount amount;
    private String status;
    private Boolean final_capture;
    private SellerProtection seller_protection;
    private String create_time;
    private String update_time;
    private String invoice_id;
    private String custom_id;
    private String seller_receivable_breakdown;

    @Data
    public static class Amount {
        private String currency_code;
        private String value;
    }

    @Data
    public static class SellerProtection {
        private String status;
        private String[] dispute_categories;
    }
}


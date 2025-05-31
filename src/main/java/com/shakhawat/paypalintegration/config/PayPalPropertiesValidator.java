package com.shakhawat.paypalintegration.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "paypal")
public class PayPalPropertiesValidator {

    private String clientId;
    private String clientSecret;
    private String mode;

    @PostConstruct
    public void validate() {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalStateException("PayPal client ID is not configured");
        }
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException("PayPal client secret is not configured");
        }
        if (!"sandbox".equalsIgnoreCase(mode) && !"live".equalsIgnoreCase(mode)) {
            throw new IllegalStateException("PayPal mode must be either 'sandbox' or 'live'");
        }
    }
}

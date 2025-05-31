package com.shakhawat.paypalintegration.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "paypal")
public class PayPalConfig {

    private String clientId;
    private String clientSecret;
    private String mode;

    // Getters and setters

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode);
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        try {
            OAuthTokenCredential credential = new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
            // Test token retrieval during startup
            String accessToken = credential.getAccessToken();
            log.info("Successfully obtained PayPal access token: {}", accessToken);
            return credential;
        } catch (PayPalRESTException e) {
            log.error("Failed to obtain PayPal access token. Please check your credentials and network connection", e);
            throw new IllegalStateException("Failed to initialize PayPal SDK", e);
        }
    }

    @Bean
    public APIContext apiContext() {
        try {
            String accessToken = oAuthTokenCredential().getAccessToken();
            APIContext context = new APIContext(accessToken);
            context.setConfigurationMap(paypalSdkConfig());
            return context;
        } catch (PayPalRESTException e) {
            log.error("Failed to create PayPal APIContext", e);
            throw new IllegalStateException("Failed to initialize PayPal APIContext", e);
        }
    }
}

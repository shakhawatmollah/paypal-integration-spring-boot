package com.shakhawat.paypalintegration;

import com.shakhawat.paypalintegration.config.PayPalConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
        PayPalConfig.class
})
@SpringBootApplication
public class PaypalIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaypalIntegrationApplication.class, args);
    }

}

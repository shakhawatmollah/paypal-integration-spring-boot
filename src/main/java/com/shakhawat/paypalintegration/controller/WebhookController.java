package com.shakhawat.paypalintegration.controller;

import com.shakhawat.paypalintegration.service.PayPalService;
import com.paypal.api.payments.Event;
import com.paypal.base.rest.JSONFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PayPalService payPalService;

    @PostMapping("/paypal")
    public ResponseEntity<Void> handlePayPalWebhook(
            @RequestBody String payload,
            @RequestHeader Map<String, String> headers) {

        log.info("Received PayPal webhook event: {}", payload);

        try {
            Event event = JSONFormatter.fromJSON(payload, Event.class);
            payPalService.processWebhookEvent(event);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing PayPal webhook", e);
            return ResponseEntity.badRequest().build();
        }
    }
}

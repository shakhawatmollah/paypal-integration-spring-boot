package com.shakhawat.paypalintegration.controller;

import com.shakhawat.paypalintegration.dto.PaymentOrderDto;
import com.shakhawat.paypalintegration.dto.PaymentResponse;
import com.shakhawat.paypalintegration.service.PayPalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PayPalService payPalService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentOrderDto paymentOrderDto) {
        PaymentResponse paymentResponse = payPalService.createPayment(paymentOrderDto);
        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, String>> paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId) {
        payPalService.executePayment(paymentId, payerId);
        return ResponseEntity.ok(Map.of("status", "success", "paymentId", paymentId));
    }

    @GetMapping("/cancel")
    public ResponseEntity<Map<String, String>> paymentCancel() {
        return ResponseEntity.ok(Map.of("status", "cancelled"));
    }
}

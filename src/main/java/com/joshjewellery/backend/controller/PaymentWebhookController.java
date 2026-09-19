package com.joshjewellery.backend.controller;

import com.joshjewellery.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments/razorpay")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Razorpay server-to-server webhook (called by Razorpay, not the frontend)")
public class PaymentWebhookController {

    private final OrderService orderService;

    @PostMapping("/webhook")
    @Operation(summary = "Razorpay webhook receiver (payment.captured / payment.failed)")
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        orderService.handleRazorpayWebhook(payload, signature);
        return ResponseEntity.ok("ok");
    }
}

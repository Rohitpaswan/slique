package com.example.paymentservice.controller;

import com.example.paymentservice.service.WebhookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor

@Slf4j
public class WebhookController {
    private final WebhookService webhookService;

    @GetMapping("/health")
    public ResponseEntity<String> check(){
        return ResponseEntity.ok("working.....");
    }

    @PostMapping("/webhook/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(@RequestBody String payload,
                                                        @RequestHeader("X-Razorpay-Signature") String signature,
                                                        @RequestHeader("X-Razorpay-Event-Id") String eventId){
        boolean valid = webhookService.verifySignature(payload, signature);
        if (!valid) {
            log.warn("Invalid Razorpay webhook signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }


        webhookService.processEvent(payload, eventId);
        return ResponseEntity.ok("Received.....");
    }
}

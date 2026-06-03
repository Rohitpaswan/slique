package com.example.paymentservice.strategy;


import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RazorpayApiClient {

    private final RazorpayClient razorpayClient;

    @Retry(name = "paymentGateway")
    @CircuitBreaker(name = "razorpayCircuit", fallbackMethod = "createLinkFallback")
    public PaymentLink createPaymentLink(JSONObject request) {
        try {
            return razorpayClient.paymentLink.create(request);
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
    }


    public PaymentLink createLinkFallback(JSONObject request, Throwable ex) {
        log.error("Razorpay failed after retries", ex);

        throw new RuntimeException("Payment gateway unavailable");
    }
}

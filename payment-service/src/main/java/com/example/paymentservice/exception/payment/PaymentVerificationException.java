package com.example.paymentservice.exception.payment;

public class PaymentVerificationException extends RuntimeException {

    private final String reason;

    public PaymentVerificationException(String reason) {
        super("Payment verification failed: " + reason);
        this.reason = reason;
    }


}
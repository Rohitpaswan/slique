package com.example.paymentservice.exception.payment;

public class PaymentOrderNotFoundException extends RuntimeException {


    public PaymentOrderNotFoundException(String message, Long orderId) {
        super(message + orderId);

    }

}
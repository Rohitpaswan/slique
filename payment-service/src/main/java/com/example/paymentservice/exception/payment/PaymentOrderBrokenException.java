package com.example.paymentservice.exception.payment;

public class PaymentOrderBrokenException extends RuntimeException{
    public PaymentOrderBrokenException(String message, Long id) {
        super(message + id);
    }
}

package com.example.paymentservice.exception.payment;



public class MissingIdempotencyKeyException extends RuntimeException{
    public MissingIdempotencyKeyException(String message){
        super(message);
    }
}

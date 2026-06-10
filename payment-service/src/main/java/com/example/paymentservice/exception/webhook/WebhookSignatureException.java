package com.example.paymentservice.exception.webhook;

public class WebhookSignatureException extends RuntimeException{
    public WebhookSignatureException(String message, Throwable cause){
        super(message, cause);
    }
}

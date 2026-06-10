package com.example.paymentservice.exception.webhook;

public class WebhookDuplicateEventException extends RuntimeException{
    public WebhookDuplicateEventException(String message){
        super(message);
    }
}

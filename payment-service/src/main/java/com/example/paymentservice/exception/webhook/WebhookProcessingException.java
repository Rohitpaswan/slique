package com.example.paymentservice.exception.webhook;

public class WebhookProcessingException extends RuntimeException{
    public WebhookProcessingException(String eventId, Throwable cause){
        super(eventId, cause);
    }
}

package com.example.paymentservice.exception.webhook;

public class WebhookOrderNotFoundException extends RuntimeException{
    public WebhookOrderNotFoundException(String mesg){
        super(mesg );
    }
}

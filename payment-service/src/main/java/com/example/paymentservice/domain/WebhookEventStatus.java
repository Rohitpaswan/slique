package com.example.paymentservice.domain;

public enum WebhookEventStatus {
    PROCESSING, //received, working on it
    PROCESSED, // done successfully
    FAILED //failed
}

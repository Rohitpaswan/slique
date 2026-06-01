package com.example.paymentservice.domain;

public enum    GatewayType {
    RAZORPAY, STRIPE;

    public static GatewayType fromPaymentMethod(PaymentMethod paymentMethod){
        return switch (paymentMethod) {
            case RAZORPAY -> RAZORPAY;
            case STRIPE -> STRIPE;
        };
    }
}

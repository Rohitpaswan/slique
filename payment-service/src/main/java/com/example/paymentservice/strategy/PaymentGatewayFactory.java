package com.example.paymentservice.strategy;

import com.example.paymentservice.domain.GatewayType;
import com.example.paymentservice.gateway.AbstractPaymentGateway;
import com.example.paymentservice.gateway.RazorpayGateway;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGatewayFactory {

    private final RazorpayGateway razorpayGateway;

    public AbstractPaymentGateway createPaymentGateway(GatewayType gatewayType) {
        return switch (gatewayType) {
            case RAZORPAY -> razorpayGateway;

            case STRIPE -> null;

        };

    }
}

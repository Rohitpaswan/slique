package com.example.paymentservice.strategy;

import com.example.paymentservice.domain.GatewayType;
import com.example.paymentservice.gateway.AbstractPaymentGateway;
import com.example.paymentservice.gateway.RazorpayGateway;
import com.razorpay.RazorpayClient;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayFactory {

    private final RazorpayClient razorpayClient;


    public PaymentGatewayFactory(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    public AbstractPaymentGateway createPaymentGateway(GatewayType gatewayType) {
        return switch (gatewayType) {
            case RAZORPAY -> {
                BankingSystem banking = new RazorpayBankingSystem(razorpayClient);
                yield new RazorpayGateway(banking, razorpayClient);
            }
            case STRIPE -> null;
        };

    }
}

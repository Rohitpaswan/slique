package com.example.paymentservice.gateway;

import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.payload.response.PaymentLinkResponse;
import com.example.paymentservice.strategy.BankingSystem;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractPaymentGateway {
    private final BankingSystem bankingSystem;

    public final PaymentLinkResponse processPayment(PaymentRequest request) {
        //validation
        if (!validatePayement(request)) throw new IllegalArgumentException("Paymetn verfication failed");

        //create payment Link
        PaymentLinkResponse paymentLinkResponse = creatPaymentLink(request);

        //marked as initiated
        markedAsInitiated(request);

        return paymentLinkResponse;
    }

    protected abstract boolean validatePayement(PaymentRequest request);

    protected abstract PaymentLinkResponse creatPaymentLink(PaymentRequest request);

    protected abstract void markedAsInitiated(PaymentRequest request);


    //verification called after user pay
    public boolean verifyPayment(PaymentRequest request) {
        return bankingSystem.processPayment(request);
    }
}

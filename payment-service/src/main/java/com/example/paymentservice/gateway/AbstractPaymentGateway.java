package com.example.paymentservice.gateway;

import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.payload.response.PaymentLinkResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractPaymentGateway {


    public final PaymentLinkResponse createPaymentFlow(PaymentRequest request) {
        //validation
        if (!validatePayement(request)) throw new IllegalArgumentException("Paymetn verfication failed");

        //create payment Link
        PaymentLinkResponse paymentLinkResponse = createPaymentLink(request);

        //marked as initiated
        markedAsInitiated(request);

        return paymentLinkResponse;
    }

    protected abstract boolean validatePayement(PaymentRequest request);

    protected abstract PaymentLinkResponse createPaymentLink(PaymentRequest request);

    protected abstract void markedAsInitiated(PaymentRequest request);


    //verification called after user pay
    public abstract boolean verifyPaymentWithProvider(PaymentRequest request);
}

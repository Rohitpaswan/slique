package com.example.paymentservice.strategy;

import com.example.paymentservice.model.PaymentRequest;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RazorpayBankingSystem implements  BankingSystem{
    private final RazorpayClient razorpayClient;

    public RazorpayBankingSystem(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    @Override
   public boolean processPayment(PaymentRequest paymentRequest) {
        try {
            Payment payment = razorpayClient.payments.fetch(String.valueOf(paymentRequest.getPaymentId()));
            String status = payment.get("status");
            if("captured".equals(status)) {
                log.info("Razorpay amount success for order: {}", paymentRequest.getOrderId());
                return true;
            }

            else{
                log.warn("Razorpay amount failed for order: {}", paymentRequest.getOrderId());
                return false;
            }
        } catch (RazorpayException e) {
            log.error("Razorpay Api call failed for order: {}", paymentRequest.getOrderId());
            return false;
        }

    }
}

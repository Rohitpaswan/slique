package com.example.paymentservice.strategy;

import com.example.paymentservice.model.PaymentRequest;

public interface BankingSystem {
     boolean processPayment(PaymentRequest paymentRequest);
}

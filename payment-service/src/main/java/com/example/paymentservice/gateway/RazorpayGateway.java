package com.example.paymentservice.gateway;

import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.payload.response.PaymentLinkResponse;
import com.example.paymentservice.strategy.BankingSystem;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

@Slf4j
public class RazorpayGateway extends AbstractPaymentGateway{
    private final RazorpayClient razorpayClient;
    public RazorpayGateway(BankingSystem bankingSystem, RazorpayClient razorpayClient) {
        super(bankingSystem);
        this.razorpayClient = razorpayClient;
    }


    @Override
    protected boolean validatePayement(PaymentRequest request) {
        if(request == null || request.getAmount() <= 0) {
            log.warn("Invalid amount: {}", request != null ? request.getAmount() : null);
            return false;
        }
        if(request.getCustomerEmail() == null || request.getCustomerEmail().isBlank()){
            log.warn("customer email is missing");
            return false;
        }

        if(!"INR".equals(request.getCurrency())){
            log.warn("Razorpay only support INR, got: {}", request.getCurrency());
            return false;
        }

        return true;
    }




    @Override
    protected PaymentLinkResponse creatPaymentLink(PaymentRequest request) {
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", request.getAmount());
        paymentLinkRequest.put("currency", "INR");
        paymentLinkRequest.put("description", "Payment for booking " + request.getOrderId());

        JSONObject customer = new JSONObject();
        customer.put("name", request.getCustomerName() );
        customer.put("email", request.getCustomerEmail());

        paymentLinkRequest.put("customer", customer);
        JSONObject notify = new JSONObject();
        notify.put("email", true);

        paymentLinkRequest.put("notify", notify);
        paymentLinkRequest.put("reminder_enable", true);
        paymentLinkRequest.put("callback_url", "http://localhost:3000/payment-success/" + request.getOrderId());
        paymentLinkRequest.put("callback_method", "get");

        try {
            PaymentLink paymentLink =  razorpayClient.paymentLink.create(paymentLinkRequest);
            PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();
            paymentLinkResponse.setGetPayment_link_id(paymentLink.get("id"));
            paymentLinkResponse.setPayment_link_url(paymentLink.get("short_url"));
            return paymentLinkResponse;

        }
        catch (RazorpayException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void markedAsInitiated(PaymentRequest request) {
        log.info("Razorpay payment link created for order: {}", request.getOrderId());

    }
}

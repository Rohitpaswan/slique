package com.example.paymentservice.gateway;

import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.payload.response.PaymentLinkResponse;
import com.example.paymentservice.strategy.RazorpayApiClient;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component("razorpayGateway")
@Slf4j
public class RazorpayGateway extends AbstractPaymentGateway{

    private final RazorpayClient razorpayClient;
    private final RazorpayApiClient razorpayApiClient;

    public RazorpayGateway(RazorpayClient razorpayClient, RazorpayApiClient razorpayApiClient) {

        this.razorpayClient = razorpayClient;
        this.razorpayApiClient = razorpayApiClient;
    }



    @Override
    @Retry(name = "paymentGateway")
    @CircuitBreaker(name = "razorpayCircuit", fallbackMethod = "verifyFallback")
    public boolean verifyPaymentWithProvider(PaymentRequest request) {
        try {
            Payment payment = razorpayClient.payments.fetch(String.valueOf(request.getPaymentId()));
            String status = payment.get("status");
            if("captured".equals(status)) {
                log.info("Razorpay amount success for order: {}", request.getOrderId());
                return true;
            }

            else{
                log.warn("Razorpay amount failed for order: {}", request.getOrderId());
                return false;
            }
        } catch (RazorpayException e) {
            log.error("Razorpay Api call failed for order: {}", request.getOrderId());
            return false;
        }

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
    protected PaymentLinkResponse createPaymentLink(PaymentRequest request) {
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

        PaymentLink paymentLink =  razorpayApiClient.createPaymentLink(paymentLinkRequest);
        PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();
        paymentLinkResponse.setGetPayment_link_id(paymentLink.get("id"));
        paymentLinkResponse.setPayment_link_url(paymentLink.get("short_url"));
        return paymentLinkResponse;


    }


    @Override
    protected void markedAsInitiated(PaymentRequest request) {
        log.info("Razorpay payment link created for order: {}", request.getOrderId());

    }

    public boolean verifyFallback(PaymentRequest request){
        log.error("CIRCUIT OPEN: Cannot verify razorpayLink: {}", request.getOrderId());
        throw new RuntimeException("Razorpay is temporarily unavailable, Try again later!!");
    }




}

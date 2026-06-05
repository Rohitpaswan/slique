package com.example.paymentservice.service;

import com.example.paymentservice.config.RazorpayConfig;
import com.example.paymentservice.domain.PaymentOrderStatus;
import com.example.paymentservice.domain.WebhookEventStatus;
import com.example.paymentservice.model.PaymentOrder;
import com.example.paymentservice.model.WebhookEventLog;
import com.example.paymentservice.repository.PaymentOrderRepository;
import com.example.paymentservice.repository.WebhookEventLogRepository;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {
    private final PaymentOrderRepository paymentOrderRepository;
    private final WebhookEventLogRepository webhookEventLogRepository;


    private final RazorpayConfig razorpayConfig;

    public boolean verifySignature(String payload, String signature){
        try {
            Utils.verifyWebhookSignature(payload, signature, razorpayConfig.getWebhookSecret());
            return true;
        } catch (RazorpayException e) {
            log.error("Signature verification failed {}", e.getMessage());
            return false;
        }
    }

    public void processEvent(String payload){
        JSONObject event = new JSONObject(payload);

        String eventType = event.getString("event");
        log.info("Processing event: {}", eventType);

        // save log before processing
        WebhookEventLog webhookEventlog = new WebhookEventLog();
        webhookEventlog.setEventType(eventType);
        webhookEventlog.setPayload(payload);
        webhookEventlog.setStatus(WebhookEventStatus.PROCESSING);
        webhookEventLogRepository.save(webhookEventlog);

        switch (eventType) {
            case "payment.link.paid"        -> handlePaymentLinkPaid(event);
            case "payment.failed"           -> handlePaymentFailed(event);
            default -> log.warn("Unhandled Razorpay event type: {}", eventType);
        }
    }



    private void handlePaymentLinkPaid(JSONObject event) {
        String paymentLinkId = event.getJSONObject("payload")
                .getJSONObject("payment_link")
                .getJSONObject("entity")
                .getString("id");
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(paymentLinkId);
        WebhookEventLog webhookEventLog = new WebhookEventLog();

        if (paymentOrder == null) {
            webhookEventLog.setStatus(WebhookEventStatus.FAILED);
            webhookEventLogRepository.save(webhookEventLog);
            throw new RuntimeException("No order for link: " + paymentLinkId);
        }

        if (PaymentOrderStatus.SUCCESS.equals(paymentOrder.getStatus())) {  // 2. Enum-to-enum
            log.info("Order {} already SUCCESS, skipping", paymentOrder.getId());

            webhookEventLog.setPaymentOrderId(paymentOrder.getId());
            webhookEventLog.setStatus(WebhookEventStatus.PROCESSED);
            webhookEventLogRepository.save(webhookEventLog);
            return;
        }
        paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
        paymentOrderRepository.save(paymentOrder);
        webhookEventLog.setPaymentOrderId(paymentOrder.getId());
        webhookEventLog.setStatus(WebhookEventStatus.PROCESSED);


    }
    private void handlePaymentFailed(JSONObject event) {
        String paymentLinkId = event.getJSONObject("payload")
                .getJSONObject("payment_link")
                .getJSONObject("entity")
                .getString("id");

        PaymentOrder order =
                paymentOrderRepository
                        .findByPaymentLinkId(paymentLinkId);

        if(order == null){
            return;
        }

        order.setStatus(PaymentOrderStatus.FAILED);

        paymentOrderRepository.save(order);
    }
}

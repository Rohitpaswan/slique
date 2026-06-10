package com.example.paymentservice.service;

import com.example.paymentservice.config.RazorpayConfig;
import com.example.paymentservice.domain.PaymentOrderStatus;
import com.example.paymentservice.domain.WebhookEventStatus;
import com.example.paymentservice.exception.webhook.WebhookOrderNotFoundException;
import com.example.paymentservice.exception.webhook.WebhookProcessingException;
import com.example.paymentservice.exception.webhook.WebhookSignatureException;
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
import org.springframework.transaction.annotation.Transactional;

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
            throw new WebhookSignatureException("Signature verification failed", e);
        }
    }

    @Transactional
    public void processEvent(String payload, String eventId){

        //Idempotency check
        if(webhookEventLogRepository.existsByEventId(eventId)){
            log.info("Event {} already processed, skipping",  eventId);
            return;
        }
        JSONObject event = new JSONObject(payload);

        String eventType = event.getString("event");
        log.info("Processing event: {}, eventId: {}", eventType, eventId);

        // save log before processing
        WebhookEventLog logEntry = new WebhookEventLog();
        logEntry.setEventType(eventType);
        logEntry.setPayload(payload);
        logEntry.setStatus(WebhookEventStatus.PROCESSING);
        logEntry.setEventId(eventId);
        webhookEventLogRepository.save(logEntry);


        try {
            switch (eventType) {
                case "payment_link.paid" -> handlePaymentLinkPaid(event, logEntry);
                case "payment.failed" -> handlePaymentFailed(event, logEntry);
                default -> log.warn("Unhandled Razorpay event type: {}", eventType);
            }

        } catch (Exception e) {
            logEntry.setStatus(WebhookEventStatus.FAILED);
            throw new WebhookProcessingException(eventId, e);
        }
        finally {
            webhookEventLogRepository.save(logEntry);
        }
    }



    private void handlePaymentLinkPaid(JSONObject event, WebhookEventLog logEntry) {
        String paymentLinkId = event.getJSONObject("payload")
                .getJSONObject("payment_link")
                .getJSONObject("entity")
                .getString("id");
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(paymentLinkId);

        if (paymentOrder == null) {
            logEntry.setStatus(WebhookEventStatus.FAILED);
            webhookEventLogRepository.save(logEntry);
            throw new WebhookOrderNotFoundException("No order for link: " + paymentLinkId);
        }

        if (PaymentOrderStatus.SUCCESS.equals(paymentOrder.getStatus())) {  // 2. Enum-to-enum
            log.info("Order {} already SUCCESS, skipping", paymentOrder.getId());

            logEntry.setPaymentOrderId(paymentOrder.getId());
            logEntry.setStatus(WebhookEventStatus.PROCESSED);
            webhookEventLogRepository.save(logEntry);
            return;
        }
        logEntry.setPaymentOrderId(paymentOrder.getId());
        logEntry.setStatus(WebhookEventStatus.PROCESSED);
        webhookEventLogRepository.save(logEntry);

        paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
        paymentOrderRepository.save(paymentOrder);


    }

    private void handlePaymentFailed(JSONObject event, WebhookEventLog logEntry) {
        String paymentLinkId = event.getJSONObject("payload")
                .getJSONObject("payment_link")
                .getJSONObject("entity")
                .getString("id");

        PaymentOrder order =
                paymentOrderRepository
                        .findByPaymentLinkId(paymentLinkId);

        if(order == null){
            logEntry.setStatus(WebhookEventStatus.FAILED);
            logEntry.setStatus(WebhookEventStatus.FAILED);
            webhookEventLogRepository.save(logEntry);
            throw new WebhookOrderNotFoundException("No order for link: " + paymentLinkId);
        }

        if(PaymentOrderStatus.INITIATED.equals(order.getStatus())){
            order.setStatus(PaymentOrderStatus.FAILED);
            paymentOrderRepository.save(order);
            logEntry.setPaymentOrderId(order.getId());
            logEntry.setStatus(WebhookEventStatus.FAILED);
            webhookEventLogRepository.save(logEntry);
        }
        else {
            log.warn("Something went wrong{}....- current Status{}", order.getId(), order.getStatus());

        }

    }
}

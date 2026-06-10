package com.example.paymentservice.repository;

import com.example.paymentservice.model.WebhookEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookEventLogRepository extends JpaRepository<WebhookEventLog, Long> {
    boolean existsByEventId(String eventId);
}

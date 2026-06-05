package com.example.paymentservice.model;

import com.example.paymentservice.domain.WebhookEventStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "webhook_event_log")
public class WebhookEventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column( unique = true, nullable = false)
    private String eventId;

    private String eventType;

    @Enumerated(EnumType.STRING)
    private WebhookEventStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private Long paymentOrderId;
    @CreationTimestamp
    private LocalDateTime createdAt;
}

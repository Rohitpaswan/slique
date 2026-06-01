package com.example.paymentservice.model;

import com.example.paymentservice.domain.PaymentMethod;
import com.example.paymentservice.domain.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity


@Table(
		name = "payment_order",
		uniqueConstraints = {

				@UniqueConstraint(name = "payment_order_idempotency_key", columnNames = "idempotency_key"),
				@UniqueConstraint(name = "payment_order_link_id", columnNames = {"payment_link_id"})
		},
		indexes = {
				// Performance Index: For looking up active payments for a booking rapidly
				@Index(
						name = "idx_booking_status",
						columnList = "booking_id, status"
				)
		}
)
public class PaymentOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private Long amount; // Smallest currency unit (e.g., Cents/Paise)

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentOrderStatus status = PaymentOrderStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false, length = 20)
	private PaymentMethod paymentMethod;

	@Column(name = "payment_link", length = 1024)
	private String paymentLink;

	@Column(name = "payment_link_id")
	private String paymentLinkId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "booking_id", nullable = false)
	private Long bookingId;

	@Column(name = "salon_id", nullable = false)
	private Long salonId;

	@Column(name = "idempotency_key", length = 50)
	private String idempotencyKey;



	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;


}

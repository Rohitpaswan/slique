package com.example.paymentservice.model;

import com.example.paymentservice.domain.PaymentMethod;
import com.example.paymentservice.domain.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PaymentOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long amount;
	
	@Column(nullable = false)
	private PaymentOrderStatus status = PaymentOrderStatus.PENDING;
	
	@Column(nullable = false)
	private PaymentMethod paymentMethod;
	
	@Column(nullable = false)
	private String paymentLink;
	
	@Column(nullable = false)
	private Long userId;
	
	@Column(nullable = false)
	private Long bookingId;
	
	@Column(nullable = false)
	private Long salonId;
}

package com.example.bookingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Long salonId;
	
	private Long customerId;
	
	private LocalDateTime startTime;
	
	private LocalDateTime endTime;
	
	@ElementCollection
	private Set<Long> serviceIds;
	
	private BookingStatus status = BookingStatus.PENDING;
	
	private Double totalPrice;
}

package com.example.bookingservice.dto;

import com.example.bookingservice.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
	private Long id;
	
	private Long salonId;
	
	private Long customerId;
	
	private LocalDateTime startTime;
	
	private LocalDateTime endTime;
	
	private Set<Long> serviceIds;

	private UserDto customer;
	private Set<ServiceOfferingDto> services;

	private SalonDto salon;

	@Builder.Default
	private BookingStatus status = BookingStatus.PENDING;
	
	private Double totalPrice;
}

package com.example.bookingservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class BookingRequest {
	
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Set<Long> serviceIds;
	
}

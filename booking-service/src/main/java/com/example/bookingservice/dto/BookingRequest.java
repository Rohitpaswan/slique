package com.example.bookingservice.dto;

import com.example.bookingservice.model.BookingStatus;
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

 //exist //10-11
//current //10:30 - 11:30

//currentStart.isBefore(existEnd) || currentEND.is
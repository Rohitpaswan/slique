package com.example.bookingservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingSlotDto {
	private LocalDateTime startDate;
	private LocalDateTime endDate;
}

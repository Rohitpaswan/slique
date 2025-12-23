package com.slique.salonservice.payload.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SalonDto {
	private Long salonId;
	private String name;
	private List<String> images;

	private String address;
	
	
	private String phoneNumber;
	
	private String email;
	
	private String city;
	
	private Long ownerId;
	
	private LocalTime openingTime;
	
	private LocalTime closingTime;
}

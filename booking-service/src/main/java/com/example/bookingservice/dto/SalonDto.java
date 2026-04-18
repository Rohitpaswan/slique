package com.example.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
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

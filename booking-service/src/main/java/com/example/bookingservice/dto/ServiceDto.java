package com.example.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDto {
	private Long id;
	private String name;
	
	private String description;
	
	private Long salonId;
	
	private Long categoryId;
	
	private String image;
	
	
	private int duration;
	
	private Double price;
}

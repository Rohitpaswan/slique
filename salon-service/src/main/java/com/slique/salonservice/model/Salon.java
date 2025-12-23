package com.slique.salonservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

public class Salon {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long salonId;
	
	@Column(nullable = false)
	private String name;
	
	@ElementCollection
	private List<String> images;
	
	@Column(nullable = false)
	private String address;
	
	@Column(nullable = false)
	private String phoneNumber;
	
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String city;
	
	@Column(nullable = false)
	private Long ownerId;
	
	@Column(nullable = false)
	private LocalTime openingTime;
	
	@Column(nullable = false)
	private LocalTime closingTime;
	
}

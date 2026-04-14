package com.slique.serviceoffering.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class ServiceOffering {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String description;
	
	@Column(nullable = false)
	private Long salonId;
	
	@Column(nullable = false)
	private Long categoryId;
	
	@Column(nullable = false)
	private String image;
	
	@Column(nullable = false)
	private int duration;
	
	@Column(nullable = false)
	private Double price;
	
}

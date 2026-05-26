package com.example.paymentservice.payload.dto;

import lombok.Data;

@Data
public class UserDto {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String role;
}

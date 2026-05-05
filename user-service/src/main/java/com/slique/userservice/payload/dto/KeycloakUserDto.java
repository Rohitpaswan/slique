package com.slique.userservice.payload.dto;

import lombok.Data;

@Data
public class KeycloakUserDto {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
}

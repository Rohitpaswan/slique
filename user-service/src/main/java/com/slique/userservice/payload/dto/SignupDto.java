package com.slique.userservice.payload.dto;

import com.slique.userservice.domain.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupDto {
	private String username;
	private String firstName;
	private String  lastName;
	private String email;
	private String phone;
	private String password;
	private UserRole userRole;
	
}

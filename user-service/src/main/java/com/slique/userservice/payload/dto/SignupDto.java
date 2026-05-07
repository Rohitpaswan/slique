package com.slique.userservice.payload.dto;

import com.slique.userservice.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupDto {
	private String username;
	private String firstName;
	private String  lastName;
	private String email;
	private String phone;
	private String password;
	private UserRole userRole;
	
}

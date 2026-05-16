package com.slique.userservice.mapper;

import com.slique.userservice.model.User;
import com.slique.userservice.payload.dto.UserDto;


public class UserMapper {
	
	public static UserDto mapToUserDto(User user){
		 return UserDto.builder()
				.id(user.getId())
				.firstName(user.getFirstName())
				 .lastName(user.getLastName())
				.email(user.getEmail())
				.role(user.getUserRole().toString())
				.build();
	}
	
}
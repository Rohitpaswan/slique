package com.slique.salonservice.mapper;

import com.slique.salonservice.model.Salon;
import com.slique.salonservice.payload.dto.SalonDto;

public class SalonMapper {
	
	public static SalonDto mapToSalonDto(Salon salon){
		return SalonDto.builder()
				.salonId(salon.getSalonId())
				.name(salon.getName())
				.email(salon.getEmail())
				.address(salon.getAddress())
				.city(salon.getCity())
				.images(salon.getImages())
				.phoneNumber(salon.getPhoneNumber())
				.ownerId(salon.getOwnerId())
				.openingTime(salon.getOpeningTime())
				.closingTime(salon.getClosingTime())
				.build();
	}
}

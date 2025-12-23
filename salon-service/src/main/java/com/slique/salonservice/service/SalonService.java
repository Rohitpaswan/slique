package com.slique.salonservice.service;

import com.slique.salonservice.model.Salon;
import com.slique.salonservice.payload.dto.SalonDto;
import com.slique.salonservice.payload.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SalonService {
	 Salon createSalon(SalonDto salonDto, UserDto userDto);
	 Salon update(SalonDto salonDto, UserDto userDto, Long salonId);
	 List<Salon> getAllSalon();
	 
	 Salon  getSalonBYId(Long salonId);
	 
	 Salon getSalonBYOwnerId(Long ownerId);
	 
	 List<Salon> searchSalonByCity(String city);
	
}

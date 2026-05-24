package com.slique.salonservice.service;

import com.slique.salonservice.model.Salon;
import com.slique.salonservice.payload.dto.SalonDto;
import com.slique.salonservice.payload.dto.UserDto;


import java.util.List;
import java.util.Set;


public interface SalonService {
	 Salon createSalon(SalonDto salonDto, UserDto userDto);
	 Salon update(SalonDto salonDto, UserDto userDto, Long salonId);
	 List<Salon> getAllSalon();
	 
	 Salon  getSalonBYId(Long salonId);
	 
	 List<Salon> getSalonBYOwnerId(Long ownerId);
	List<Salon> getSalonsByIds(Set<Long> salonIds);
	 List<Salon> searchSalonByCity(String city);
	void deleteSalon(Long salonId, UserDto userDto);
	
}

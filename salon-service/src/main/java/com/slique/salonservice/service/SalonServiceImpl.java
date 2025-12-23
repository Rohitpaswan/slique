package com.slique.salonservice.service;

import com.slique.salonservice.model.Salon;
import com.slique.salonservice.payload.dto.SalonDto;
import com.slique.salonservice.payload.dto.UserDto;
import com.slique.salonservice.repository.SalonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalonServiceImpl implements SalonService {
	private final SalonRepository salonRepository;
	
	public SalonServiceImpl(SalonRepository salonRepository) {
		this.salonRepository = salonRepository;
	}
	
	@Override
	public Salon createSalon(SalonDto salonDto, UserDto userDto) {
		Salon salon = Salon.builder()
				.name(salonDto.getName())
				.email(salonDto.getEmail())
				.images(salonDto.getImages())
				.address(salonDto.getAddress())
				.phoneNumber(salonDto.getPhoneNumber())
				.city(salonDto.getCity())
				.ownerId(userDto.getId())
				.openingTime(salonDto.getOpeningTime())
				.closingTime(salonDto.getClosingTime())
				.build();
		return salonRepository.save(salon);
	}
	
	@Override
	public Salon update(SalonDto salonDto, UserDto userDto, Long salonId) {
		if( ! salonRepository.existsById(salonDto.getSalonId())){
			 throw new RuntimeException("Salon not found");
		}
		Salon salon = Salon.builder()
				.name(salonDto.getName())
				.email(salonDto.getEmail())
				.images(salonDto.getImages())
				.address(salonDto.getAddress())
				.phoneNumber(salonDto.getPhoneNumber())
				.city(salonDto.getCity())
				.ownerId(userDto.getId())
				.openingTime(salonDto.getOpeningTime())
				.closingTime(salonDto.getClosingTime())
				.build();
		return salonRepository.save(salon);
	}
	
	@Override
	public List<Salon> getAllSalon() {
		List<Salon> salons = 	salonRepository.findAll();
		return salons;
	}
	
	@Override
	public Salon getSalonBYId(Long salonId) {
		Optional<Salon> salon = salonRepository.findById(salonId);
		if(salon.isPresent()){
			return salon.get();
		}
		throw new RuntimeException("Salon not found with salonId: " + salonId);
	}
	
	@Override
	public Salon getSalonBYOwnerId(Long ownerId) {
			Optional<Salon> salon = salonRepository.findByownerId(ownerId);
			if(salon.isPresent()){
				return  salon.get();
			}
			throw new RuntimeException("Salon not found with ownerId: " + ownerId);
	}
	
	@Override
	public List<Salon> searchSalonByCity(String city) {
		return salonRepository.searchSalonByCity(city);
	}
}

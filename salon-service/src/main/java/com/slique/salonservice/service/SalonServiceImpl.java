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
		//chech it is owner or admin

		//check for multiple salon
		boolean alreadyExists = salonRepository.existsByAddressAndCity(
				salonDto.getName(),
				salonDto.getCity()
		);

		if (alreadyExists) {
			throw new RuntimeException("You already have a salon named '"
					+ salonDto.getName() + "' in " + salonDto.getCity());
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
	public Salon update(SalonDto salonDto, UserDto userDto, Long salonId) {
		if( ! salonRepository.existsById(salonDto.getSalonId())){
			 throw new RuntimeException("Salon not found");
		}

		if(!salonDto.getOwnerId().equals(userDto.getId())) throw new RuntimeException("Not Auhthrizaie");

		Salon salon = Salon.builder()
				.salonId(salonDto.getSalonId())
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
		return 	salonRepository.findAll();
		
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
	public List<Salon> getSalonBYOwnerId(Long ownerId) {
		List<Salon> salons = salonRepository.findByownerId(ownerId);
		
		if (!salons.isEmpty()) {
			return salons;
		}
		
		throw new RuntimeException("Salon not found with ownerId: " + ownerId);
	}
	
	@Override
	public List<Salon> searchSalonByCity(String city) {
		return salonRepository.searchSalonByCity(city);
	}

	@Override
	public void deleteSalon(Long salonId, UserDto userDto) {
		Salon salon = salonRepository.findById(salonId)
				.orElseThrow(() -> new RuntimeException("Salon not found with salonId: " + salonId));

		if (!salon.getOwnerId().equals(userDto.getId())) {
			throw new RuntimeException("You are not authorized to delete this salon");
		}

		salonRepository.deleteById(salonId);
	}
}

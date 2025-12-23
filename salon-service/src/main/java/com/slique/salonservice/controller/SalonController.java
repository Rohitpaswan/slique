package com.slique.salonservice.controller;

import com.slique.salonservice.mapper.SalonMapper;
import com.slique.salonservice.model.Salon;
import com.slique.salonservice.payload.dto.SalonDto;
import com.slique.salonservice.payload.dto.UserDto;
import com.slique.salonservice.service.SalonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Salon-related operations.
 * Provides endpoints for CRUD operations and searching salons by location.
 */
@RestController
@RequestMapping("/api/salons")
public class SalonController {
	
	private final SalonService salonService;
	
	public SalonController(SalonService salonService) {
		this.salonService = salonService;
	}
	
	/**
	 * Retrieves all registered salons.
	 * * @return List of SalonDto with HTTP Status 200 (OK)
	 */
	@GetMapping(path = "/", produces = "application/json")
	public ResponseEntity<List<SalonDto>> getAllSalon() {
		List<Salon> salons = salonService.getAllSalon();
		
		// Transform Domain Entities to DTOs to encapsulate internal structure
		List<SalonDto> salonDtos = salons.stream()
				.map(SalonMapper::mapToSalonDto)
				.toList();
		
		return new ResponseEntity<>(salonDtos, HttpStatus.OK);
	}
	
	/**
	 * Creates a new salon entry.
	 * Note: Currently uses a mocked UserDto (ID: 1) for ownership assignment.
	 * * @param salonDto Data for the new salon
	 * @return The created SalonDto with HTTP Status 201 (Created)
	 */
	@PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
	public ResponseEntity<SalonDto> createSalon(@RequestBody SalonDto salonDto) {
		// TODO: Replace hardcoded UserDto with authentication principal/context
		UserDto userDto = new UserDto();
		userDto.setId(1L);
		
		Salon salon = salonService.createSalon(salonDto, userDto);
		SalonDto addSalonDto = SalonMapper.mapToSalonDto(salon);
		
		return new ResponseEntity<>(addSalonDto, HttpStatus.CREATED);
	}
	
	/**
	 * Updates an existing salon.
	 * * @param salonDto Updated data
	 * @param salonId  The ID of the salon to modify
	 * @return The updated SalonDto with HTTP Status 200 (OK)
	 */
	@PutMapping(path = "/update", consumes = "application/json", produces = "application/json")
	public ResponseEntity<SalonDto> updateSalon(@RequestBody SalonDto salonDto, Long salonId) {
		// TODO: Replace hardcoded UserDto with security context
		UserDto userDto = new UserDto();
		userDto.setId(1L);
		
		Salon salon = salonService.update(salonDto, userDto, salonId);
		SalonDto updatedSalon = SalonMapper.mapToSalonDto(salon);
		
		return new ResponseEntity<>(updatedSalon, HttpStatus.OK);
	}
	
	/**
	 * Fetches a single salon by its primary identifier.
	 * * @param salonId Unique ID of the salon
	 * @return SalonDto with HTTP Status 200 (OK)
	 */
	@GetMapping(path = "/{salonId}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<SalonDto> getSalonById(@PathVariable Long salonId) {
		Salon salon = salonService.getSalonBYId(salonId);
		return new ResponseEntity<>(SalonMapper.mapToSalonDto(salon), HttpStatus.OK);
	}
	
	/**
	 * Fetches salon details based on the owner's ID.
	 * * @param ownerId Unique ID of the user who owns the salon
	 * @return SalonDto with HTTP Status 200 (OK)
	 */
	@GetMapping(path = "/{ownerId}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<SalonDto> getSalonBYOwnerId(@PathVariable Long ownerId) {
		Salon salon = salonService.getSalonBYOwnerId(ownerId);
		return new ResponseEntity<>(SalonMapper.mapToSalonDto(salon), HttpStatus.OK);
	}
	
	/**
	 * Searches for salons located in a specific city.
	 * * @param city Name of the city to filter by
	 * @return List of matching SalonDtos
	 */
	@GetMapping(path = "/search",  produces = "application/json")
	public ResponseEntity<List<SalonDto>> searchSalonByCity(@RequestParam("city") String city) {
		List<Salon> salons = salonService.searchSalonByCity(city);
		
		List<SalonDto> salonDto = salons.stream()
				.map(SalonMapper::mapToSalonDto)
				.toList();
		
		return new ResponseEntity<>(salonDto, HttpStatus.OK);
	}
}
package com.slique.serviceoffering.controller;


import com.slique.serviceoffering.dto.CategoryDto;
import com.slique.serviceoffering.dto.SalonDto;
import com.slique.serviceoffering.dto.ServiceDto;
import com.slique.serviceoffering.model.ServiceOffering;
import com.slique.serviceoffering.service.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-offering/owner")
@RequiredArgsConstructor
public class SalonServiceOfferingController {
	private final ServiceOfferingService serviceOfferingService;
	
	@PostMapping(consumes = "application/json", produces = "application/json")
	public ResponseEntity<ServiceOffering> createServiceOffering( @RequestBody ServiceDto serviceDto){
		SalonDto salonDto = new SalonDto();
		salonDto.setSalonId(1L);
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId(serviceDto.getCategoryId());
		
		ServiceOffering service =
				serviceOfferingService.createServiceOffering( salonDto, serviceDto, categoryDto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(service);
	}
	
	@PutMapping (path = "/{serviceId}", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ServiceOffering> updateServiceOffering(@PathVariable("serviceId") Long id,  @RequestBody ServiceDto serviceDto){
		SalonDto salonDto = new SalonDto();
		salonDto.setSalonId(1L);
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setId(serviceDto.getCategoryId());
		
		ServiceOffering service =
				serviceOfferingService.updateServiceOffering( id, serviceDto);
		
		return ResponseEntity.status(HttpStatus.OK).body(service);
	}
	
	
}

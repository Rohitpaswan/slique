package com.slique.serviceoffering.controller;

import com.slique.serviceoffering.model.ServiceOffering;
import com.slique.serviceoffering.service.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/service-offering")
@RequiredArgsConstructor
public class ServiceOfferingController {
	private final ServiceOfferingService serviceOfferingService;
	
	@GetMapping(path = "/salon/{salonId}")
	public ResponseEntity<Set<ServiceOffering>> getAllServiceOffering(
			@PathVariable Long salonId, @RequestParam(required = false) Long categoryId){
		return ResponseEntity.ok(serviceOfferingService.getAllServiceBySalonId(salonId, categoryId));
	}
	
	@GetMapping(path = "/list/{ids}")
	public ResponseEntity<Set<ServiceOffering>> getServicesByIds(@PathVariable Set<Long> ids){
		return ResponseEntity.ok(serviceOfferingService.getServicesByIds(ids));
	
	}
	
	@GetMapping(path = "/{serviceOfferingId}")
	public ResponseEntity<ServiceOffering> getServiceById(
			@PathVariable("serviceOfferingId") Long id){
		return ResponseEntity.ok(serviceOfferingService.getServiceById(id));
	}
}

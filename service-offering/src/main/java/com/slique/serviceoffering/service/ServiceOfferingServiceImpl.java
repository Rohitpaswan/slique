package com.slique.serviceoffering.service;

import com.slique.serviceoffering.dto.CategoryDto;
import com.slique.serviceoffering.dto.SalonDto;
import com.slique.serviceoffering.dto.ServiceDto;
import com.slique.serviceoffering.model.ServiceOffering;
import com.slique.serviceoffering.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceOfferingServiceImpl implements ServiceOfferingService{
	private final ServiceRepository serviceRepository;
	@Override
	public ServiceOffering createServiceOffering(SalonDto salonDto, ServiceDto serviceDto, CategoryDto categoryDto) {
		ServiceOffering savedService = 	ServiceOffering.builder()
					.name(serviceDto.getName())
					.description(serviceDto.getDescription())
					.image(serviceDto.getImage())
					.duration(serviceDto.getDuration())
					.price(serviceDto.getPrice())
					.salonId(salonDto.getSalonId())
					.categoryId(categoryDto.getId())
					.build();
			
		return serviceRepository.save(savedService);
	}
	
	
	@Override
	public ServiceOffering updateServiceOffering(Long salonId, ServiceDto serviceDto) {
		
		ServiceOffering serviceOffering = serviceRepository.findById(serviceDto.getId())
				.orElseThrow(() -> new RuntimeException("Service offering not found"));
		
		// Optional security check (recommended)
		if (!serviceOffering.getSalonId().equals(salonId)) {
			throw new RuntimeException("Unauthorized update attempt");
		}
		
		// Update fields
		serviceOffering.setName(serviceDto.getName());
		serviceOffering.setDescription(serviceDto.getDescription());
		serviceOffering.setImage(serviceDto.getImage());
		serviceOffering.setDuration(serviceDto.getDuration());
		serviceOffering.setPrice(serviceDto.getPrice());
		serviceOffering.setCategoryId(serviceDto.getCategoryId());
		
		return serviceRepository.save(serviceOffering);
	}
	
	@Override
	public Set<ServiceOffering> getServicesByIds(Set<Long> ids) {
		return new HashSet<>(serviceRepository.findAllById(ids));
	}
	
	@Override
	public Set<ServiceOffering> getAllServiceBySalonId(Long salonId, Long categoryId) {
		Set<ServiceOffering> serviceOfferings =  serviceRepository.findBySalonId(salonId);
		
		if(categoryId != null){
		serviceOfferings =	serviceOfferings.stream().filter(serviceOffering -> serviceOffering.getCategoryId() != null
			&& serviceOffering.getCategoryId().equals(categoryId)).collect(Collectors.toSet());
		}
		return serviceOfferings;
	}
	
	@Override
	public ServiceOffering getServiceById(Long serviceId) {
		return serviceRepository.findById(serviceId)
				.orElseThrow(() -> new RuntimeException("Service not found " + serviceId));
	}
	
	@Override
	public void deleteServiceById(Long serviceId, Long salonId) {
		ServiceOffering serviceOffering = serviceRepository.findById(serviceId)
				.orElseThrow(() -> new RuntimeException("Service not found"));
		if(!serviceOffering.getSalonId().equals(salonId)) {
			throw new RuntimeException("Service not found");
		}
		serviceRepository.delete(serviceOffering);
	}
}

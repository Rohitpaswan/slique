package com.slique.serviceoffering.service;

import com.slique.serviceoffering.dto.CategoryDto;
import com.slique.serviceoffering.dto.SalonDto;
import com.slique.serviceoffering.dto.ServiceDto;
import com.slique.serviceoffering.model.ServiceOffering;


import java.util.Set;

public interface ServiceOfferingService {
	ServiceOffering createServiceOffering(SalonDto salonDto, ServiceDto serviceDto, CategoryDto categoryDto);
	ServiceOffering updateServiceOffering(Long serviceId, ServiceDto serviceDto);
	Set<ServiceOffering> getServicesByIds(Set<Long> ids);
	Set<ServiceOffering> getAllServiceBySalonId(Long salonId, Long categoryId);
	ServiceOffering getServiceById(Long serviceId);
	void deleteServiceById(Long serviceId, Long salonId);
	
	
}

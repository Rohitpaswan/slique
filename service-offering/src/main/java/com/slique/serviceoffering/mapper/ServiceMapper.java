package com.slique.serviceoffering.mapper;
import com.slique.serviceoffering.dto.ServiceDto;
import com.slique.serviceoffering.model.ServiceOffering;



public class ServiceMapper {
	
	private ServiceMapper() {}
	
	public static ServiceOffering mapToSericeOffering(ServiceDto serviceDto){
	return ServiceOffering.builder()
			.id(serviceDto.getId())
			.name(serviceDto.getName())
			.description(serviceDto.getDescription())
			.image(serviceDto.getImage())
			.duration(serviceDto.getDuration())
			.price(serviceDto.getPrice())
			.salonId(serviceDto.getSalonId())
			.categoryId(serviceDto.getCategoryId())
			.build();
	
	}
	
	
	public static ServiceDto mapToServiceDto(ServiceOffering serviceOffering){
		return ServiceDto.builder()
				.id(serviceOffering.getId())
				.name(serviceOffering.getName())
				.description(serviceOffering.getDescription())
				.image(serviceOffering.getImage())
				.duration(serviceOffering.getDuration())
				.price(serviceOffering.getPrice())
				.salonId(serviceOffering.getSalonId())
				.categoryId(serviceOffering.getCategoryId())
				.build();
	}
}

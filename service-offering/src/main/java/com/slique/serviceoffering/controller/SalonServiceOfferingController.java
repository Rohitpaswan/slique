package com.slique.serviceoffering.controller;


import com.slique.serviceoffering.dto.CategoryDto;
import com.slique.serviceoffering.dto.SalonDto;
import com.slique.serviceoffering.dto.ServiceDto;
import com.slique.serviceoffering.model.ServiceOffering;
import com.slique.serviceoffering.service.ServiceOfferingService;
import com.slique.serviceoffering.service.client.CategoryFeignClient;
import com.slique.serviceoffering.service.client.SalonFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/service-offering/owner")
@RequiredArgsConstructor
public class SalonServiceOfferingController {
    private final ServiceOfferingService serviceOfferingService;
    private final SalonFeignClient salonFeignClient;
    private final CategoryFeignClient categoryFeignClient;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<ServiceOffering> createServiceOffering(@RequestBody ServiceDto serviceDto, @RequestHeader("Authorization") String jwt) {

        SalonDto selectedSalon = validateSalonOwnership(jwt, serviceDto.getSalonId());
        CategoryDto categoryDto = getValidatedCategory(serviceDto.getCategoryId());
        ServiceOffering service = serviceOfferingService.createServiceOffering(selectedSalon, serviceDto, categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }

    @PutMapping(path = "/{serviceId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ServiceOffering> updateServiceOffering(@PathVariable("serviceId") Long serviceId, @RequestBody ServiceDto serviceDto, @RequestHeader("Authorization") String jwt) {
        SalonDto selectedSalon = validateSalonOwnership(jwt, serviceDto.getSalonId());
        getValidatedCategory(serviceDto.getCategoryId());
        ServiceOffering service = serviceOfferingService.updateServiceOffering(serviceId, selectedSalon.getSalonId(), serviceDto);
        return ResponseEntity.status(HttpStatus.OK).body(service);
    }


    private SalonDto validateSalonOwnership(String jwt, Long salonId) {
        List<SalonDto> salonDtos = salonFeignClient.getSalonBYOwnerId(jwt).getBody();
        log.info("salonDto", salonDtos);
        if (salonDtos == null || salonDtos.isEmpty()) {
            throw new RuntimeException("No salons found for this owner");
        }
        return salonDtos.stream().filter(salon -> salon.getSalonId().equals(salonId)).findFirst().orElseThrow(() -> new RuntimeException("Salon not found or unauthorized"));
    }


    private CategoryDto getValidatedCategory(Long categoryId) {
        CategoryDto categoryDto = categoryFeignClient.getCategoryById(categoryId).getBody();
        if (categoryDto == null) {
            throw new RuntimeException("Category not found");
        }
        return categoryDto;
    }
}

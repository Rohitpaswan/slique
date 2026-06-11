package com.example.bookingservice.service.client;

import com.example.bookingservice.dto.ServiceOfferingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;



@FeignClient(name = "service-offering", path = "/api/service-offering")
public interface ServiceOfferingFeignClient {

    @GetMapping(path = "/lists")
    ResponseEntity<Set<ServiceOfferingDto>> getServicesByIds(@RequestParam Set<Long> ids);
}

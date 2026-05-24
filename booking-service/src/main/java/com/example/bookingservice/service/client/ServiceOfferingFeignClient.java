package com.example.bookingservice.service.client;

import com.example.bookingservice.dto.ServiceOfferingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;



@FeignClient(name = "service-offering", path = "/api/service-offering")
public interface ServiceOfferingFeignClient {

    @GetMapping(path = "/list/{ids}")
    ResponseEntity<Set<ServiceOfferingDto>> getServicesByIds(@PathVariable Set<Long> ids);
}

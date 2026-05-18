package com.slique.serviceoffering.service.client;

import com.slique.serviceoffering.dto.SalonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "SALON", path = "/api/salons")
public interface SalonFeignClient {
    @GetMapping(path = "/owner",  produces = "application/json" )
     ResponseEntity<List<SalonDto>> getSalonBYOwnerId(@RequestHeader("Authorization") String jwt);
}

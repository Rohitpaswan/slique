package com.slique.categoryservice.service.client;

import com.slique.categoryservice.dto.SalonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "SALON", path = "/api/salons")
public interface SalonFeignClient {
    @GetMapping(path = "/owner",  produces = "application/json" )
    ResponseEntity<List<SalonDto>> getSalonBYOwnerId(@RequestHeader("Authorization") String jwt);

    @GetMapping("/{salonId}")
    ResponseEntity<SalonDto> getSalonById(@PathVariable Long salonId);
}

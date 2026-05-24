package com.example.bookingservice.service.client;


import com.example.bookingservice.dto.SalonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(name = "SALON", path = "/api/salons")
public interface SalonFeignClient {

    @GetMapping(path = "/owner",  produces = "application/json" )
    ResponseEntity<List<SalonDto>> getSalonBYOwnerId(@RequestHeader("Authorization") String jwt);


    @GetMapping("/api/salons/{salonId}")
     ResponseEntity<SalonDto> getSalonById(@PathVariable Long salonId);


    @PostMapping("/batch")
    ResponseEntity<List<SalonDto>> getSalonsByIds(@RequestBody Set<Long> salonIds);
}

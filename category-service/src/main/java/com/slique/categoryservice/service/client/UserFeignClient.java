package com.slique.categoryservice.service.client;

import com.slique.categoryservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER", path = "/api/users")
public interface UserFeignClient {
    @GetMapping("/profile")
     ResponseEntity<UserDto> getUserFromJwtToken(@RequestHeader("Authorization") String jwt);

}
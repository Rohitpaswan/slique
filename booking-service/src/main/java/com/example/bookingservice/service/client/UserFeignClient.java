package com.example.bookingservice.service.client;

import com.example.bookingservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


import java.util.List;
import java.util.Set;

@FeignClient(name = "USER", path = "/api/users")
public interface UserFeignClient {
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserFromJwtToken(@RequestHeader("Authorization") String jwt);

    @GetMapping("/{userId}")
     ResponseEntity<UserDto> getUserById(@PathVariable Long userId);

    @PostMapping("/batch")
    ResponseEntity<List<UserDto>> getUsersByIds(Set<Long> customerIds);
}

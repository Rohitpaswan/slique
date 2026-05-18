package com.slique.serviceoffering.service.client;

import com.slique.serviceoffering.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "USER", path = "/api/users")
public interface UserFeignClient {
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserFromJwtToken(@RequestHeader("Authorization") String jwt);

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId);

}

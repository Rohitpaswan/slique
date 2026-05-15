package com.slique.userservice.controllers;

import com.slique.userservice.payload.dto.LoginDto;
import com.slique.userservice.payload.dto.SignupDto;
import com.slique.userservice.payload.response.AuthResponse;
import com.slique.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupDto signupDto){
        AuthResponse authResponse = authService.signUp(signupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto req){
        AuthResponse authResponse = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @GetMapping("/access-token/refresh-token/{refreshToken}")
    public ResponseEntity<AuthResponse> getAccessTokenHandler(
            @PathVariable String refreshToken) throws Exception {

        AuthResponse response = authService.getAccessTokenFromRefreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}

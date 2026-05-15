package com.slique.userservice.service;

import com.slique.userservice.payload.dto.SignupDto;
import com.slique.userservice.payload.response.AuthResponse;

public interface AuthService {
    AuthResponse signUp(SignupDto signupDto);
    AuthResponse login(String email, String password);
    AuthResponse getAccessTokenFromRefreshToken(String refreshToken);
}

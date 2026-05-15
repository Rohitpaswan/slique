package com.slique.userservice.service.impl;

import com.slique.userservice.model.User;
import com.slique.userservice.payload.dto.SignupDto;
import com.slique.userservice.payload.request.TokenRequest;
import com.slique.userservice.payload.response.AuthResponse;
import com.slique.userservice.payload.response.TokenResponse;
import com.slique.userservice.repository.UserRepository;
import com.slique.userservice.service.AuthService;
import com.slique.userservice.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final KeycloakService keycloakService;
    private final UserRepository userRepository;

    @Override
    public AuthResponse signUp(SignupDto signupDto) {
        //create user in keycloak
        keycloakService.createUser(signupDto);

        //save in db
        User user = User.builder()
                .username(signupDto.getUsername())
                .firstName(signupDto.getFirstName())
                .lastName(signupDto.getLastName())
                .email(signupDto.getEmail())
                .phone(signupDto.getPhone())
                .userRole(signupDto.getUserRole())
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        TokenResponse tokenResponse = keycloakService.tokenGeneration(TokenRequest.builder()
                .username(signupDto.getUsername())
                .password(signupDto.getPassword())
                .grantType("password")
                .build());

   return AuthResponse.builder()
                .jwt(tokenResponse.getAccessToken())
                .refresh_token(tokenResponse.getRefreshToken())
                .title("welcome " + user.getEmail())
                .message("Register successful").build();
    }


    public AuthResponse getAccessTokenFromRefreshToken(String refershToken){
        TokenResponse tokenResponse = keycloakService.tokenGeneration(
                TokenRequest.builder()
                        .grantType("refresh_token")
                        .refreshToken(refershToken)
                        .build()
        );

        return AuthResponse.builder()
                .message("Access Token received")
                .jwt(tokenResponse.getAccessToken())
                .refresh_token(tokenResponse.getRefreshToken())
                .build();
    }
    @Override
  public  AuthResponse login(String email, String password){
        TokenResponse tokenResponse = keycloakService.tokenGeneration(
                TokenRequest.builder()
                        .username(email)
                        .password(password)
                        .grantType("password")
                        .build()
        );

      return AuthResponse.builder()
              .jwt(tokenResponse.getAccessToken())
              .refresh_token(tokenResponse.getRefreshToken())
              .title("welcome " + email)
              .message("login successful").build();
    }
}



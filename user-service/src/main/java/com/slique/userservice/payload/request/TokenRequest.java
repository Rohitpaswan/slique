package com.slique.userservice.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRequest {
    private String username;
    private String password;
    private String refreshToken;
    private String grantType;
}

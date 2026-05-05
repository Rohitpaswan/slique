package com.slique.userservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakConfig {
    private String baseUrl;
    private String realm;
    private String adminUrl;
    private String tokenUrl;
    private String clientId;
    private String clientSecret;
    private String clientUuid;
    private String scope;
}

package com.slique.userservice.service;

import com.slique.userservice.config.KeycloakConfig;
import com.slique.userservice.domain.KeycloakRole;
import com.slique.userservice.exception.KeycloakIntegrationException;
import com.slique.userservice.exception.ResourceNotFoundException;
import com.slique.userservice.exception.TokenGenerationException;
import com.slique.userservice.payload.dto.KeycloakUserDto;
import com.slique.userservice.payload.dto.SignupDto;
import com.slique.userservice.payload.request.Credential;
import com.slique.userservice.payload.request.TokenRequest;
import com.slique.userservice.payload.request.UserRequest;
import com.slique.userservice.payload.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final KeycloakConfig keycloakConfig;

    public void createUser(SignupDto signupDto) {
        TokenResponse tokenResponse = tokenGeneration(TokenRequest.builder().grantType("client_credentials").build());
        String ACCESS_TOKEN = tokenResponse.getAccessToken();

        List<Credential> credentials = new ArrayList<>();
        Credential credential = Credential.builder()
                .type("password")
                .value(signupDto.getPassword())
                .temporary(false)
                .build();
        credentials.add(credential);

        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(signupDto.getUsername());
        userRequest.setFirstName(signupDto.getFirstName());
        userRequest.setLastName(signupDto.getLastName());
        userRequest.setEmail(signupDto.getEmail());
        userRequest.setEmailVerified(false);
        userRequest.setEnabled(true);
        userRequest.setCredentials(credentials);

        //create http header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(ACCESS_TOKEN);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        //create http entity
        HttpEntity<UserRequest> httpEntity = new HttpEntity<>(userRequest, httpHeaders);
        try {
            ResponseEntity<String> response = restTemplate.exchange(keycloakConfig.getAdminUrl(), HttpMethod.POST, httpEntity, String.class);

            log.info("user created {}", response);

            //fetch created user from keycloak
            KeycloakUserDto user = fetchFirstUserByUsername(signupDto.getUsername(), ACCESS_TOKEN);

            //get roles created in keycloak client-app
            KeycloakRole keycloakRoles = getUserRole(keycloakConfig.getClientUuid(), ACCESS_TOKEN, signupDto.getUserRole().toString());
            List<KeycloakRole> roles = new ArrayList<>();
            roles.add(keycloakRoles);
            assignKeyCloak(user.getId(), keycloakConfig.getClientUuid(), roles, ACCESS_TOKEN);

        } catch (Exception e) {
            log.error(e.getMessage());
           throw new KeycloakIntegrationException("Failed to connected with keycloak");
        }


    }

    private void assignKeyCloak(String userId, String clientUuid, List<KeycloakRole> roles, String accessToken) {
        String url = keycloakConfig.getBaseUrl() + "/admin/realms/slique/users/" + userId + "/role-mappings/clients/" + clientUuid;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<List<KeycloakRole>> entity = new HttpEntity<>(roles, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            response.getStatusCode();

        } catch (Exception e) {

            throw new KeycloakIntegrationException("Failed to assign roles: " + e.getMessage());
        }
    }


    //generate toke
    public TokenResponse tokenGeneration(TokenRequest tokenRequest) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = getMultiValueMapHttpEntity(tokenRequest, httpHeaders);
        try {
            ResponseEntity<TokenResponse> responseResponseEntity = restTemplate.exchange(
                    keycloakConfig.getTokenUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    TokenResponse.class);

            log.info("token response {}", responseResponseEntity);
            return responseResponseEntity.getBody();

        } catch (Exception e) {

            log.error("Token generation error: {}", e.getMessage(), e);
            throw new TokenGenerationException("Failed to generate token: " + e.getMessage());

        }


    }

    private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(TokenRequest tokenRequest, HttpHeaders httpHeaders) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", keycloakConfig.getClientId());
        requestBody.add("client_secret", keycloakConfig.getClientSecret());
        requestBody.add("grant_type", tokenRequest.getGrantType());
        requestBody.add("scope", keycloakConfig.getScope());

        if (tokenRequest.getUsername() != null)  requestBody.add("username", tokenRequest.getUsername());
        if(tokenRequest.getPassword() != null)    requestBody.add("password", tokenRequest.getPassword());
        if(tokenRequest.getRefreshToken() != null)    requestBody.add("refresh_token", tokenRequest.getRefreshToken());
        return new HttpEntity<>(requestBody, httpHeaders);

    }

    public KeycloakRole getUserRole(String clientUuid, String token, String role) {

        //end point url to fetch user
        String url = keycloakConfig.getBaseUrl() + "/admin/realms/slique/clients/{clientId}/roles/{role}";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        httpHeaders.set("Accept", "application/json");

        //http entity
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<KeycloakRole> keycloakRoles = restTemplate.exchange(url, HttpMethod.GET, httpEntity, KeycloakRole.class, clientUuid, role);
        log.info("keycloak{}", keycloakRoles);
        return keycloakRoles.getBody();
    }

    public KeycloakUserDto fetchFirstUserByUsername(String username, String token) {
        String url = keycloakConfig.getBaseUrl() + "/admin/realms/slique/users?username=" + username;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        // Create an HttpEntity with the headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Send the GET request
            ResponseEntity<KeycloakUserDto[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, KeycloakUserDto[].class);

            // Extract and return the first user object
            KeycloakUserDto[] users = response.getBody();
            if (users != null && users.length > 0) {
                return users[0]; // Return the first user
            } else {
                throw new ResourceNotFoundException("No users found for username: " + username);
            }

        } catch (Exception e) {
            log.error("Failed to fetch Keycloak user details: {}", username);
            throw new KeycloakIntegrationException("Failed to fetch user details: " + e.getMessage());
        }


    }


    public KeycloakUserDto fetchUserProfileByJwt(String token)  {
        String url = keycloakConfig.getBaseUrl() +"/realms/slique/protocol/openid-connect/userinfo";

       HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization",  token);


        // Create an HttpEntity with the headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Send the GET request
            ResponseEntity<KeycloakUserDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    KeycloakUserDto.class
            );

            // Extract and return the first user object
            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to fetch user details: {}", e.getMessage());
            throw new KeycloakIntegrationException("Failed to fetch user details: " + e.getMessage());
        }
    }

}

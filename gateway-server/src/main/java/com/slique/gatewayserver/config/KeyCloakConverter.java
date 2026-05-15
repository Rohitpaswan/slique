package com.slique.gatewayserver.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class KeyCloakConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Realm roles
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            roles.forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            );
        }

        // Client roles (this is where your ADMIN role actually lives)
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            resourceAccess.values().forEach(clientDetails -> {
                if (clientDetails instanceof Map<?, ?> clientMap) {
                    Object roles = clientMap.get("roles");
                    if (roles instanceof List<?> clientRoles) {
                        clientRoles.stream()
                                .filter(String.class::isInstance)
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + ((String) r).toUpperCase()))
                                .forEach(authorities::add);
                    }
                }
            });
        }

        return authorities;
    }
}
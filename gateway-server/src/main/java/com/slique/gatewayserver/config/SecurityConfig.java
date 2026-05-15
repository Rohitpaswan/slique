package com.slique.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/auth/**").permitAll()
                        // 2. SALON_OWNER-only paths FIRST (more specific)
                        .pathMatchers(
                                "/api/categories/salon-owner/**",
                                "/api/notifications/salon-owner/**",
                                "/api/service-offering/salon-owner/**"
                        ).hasRole("SALON_OWNER")

                        // 3. Broader authenticated paths AFTER
                        .pathMatchers(
                                "/api/salons/**",
                                "/api/categories/**",
                                "/api/notifications/**",
                                "/api/bookings/**",
                                "/api/payments/**",
                                "/api/service-offering/**",
                                "/api/users/**",
                                "/api/reviews/**"
                        ).hasAnyRole("CUSTOMER", "SALON_OWNER", "ADMIN")

                        // 4. Deny everything else by default
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtSpac -> jwtSpac.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}

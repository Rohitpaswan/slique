package com.slique.gatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
	
	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				
				.route("USER", r -> r
						.path("/api/users", "/api/users/**")
						.uri("lb://user"))
				
				.route("SALON", r -> r
						.path("/salons/**", "/api/salons/**", "/api/admin/salons/**")
						.uri("lb://SALON"))
				
				.route("SERVICE-OFFERING", r -> r
						.path("/api/service-offering/**")
						.uri("lb://SERVICE-OFFERING"))
				
				.route("CATEGORY", r -> r
						.path("/api/categories/**")
						.uri("lb://CATEGORY"))
				
				.route("BOOKING", r -> r
						.path("/api/bookings/**")
						.uri("lb://BOOKING"))
				
				.route("PAYMENT", r -> r
						.path("/api/payments/**")
						.uri("lb://PAYMENT"))
				
				.route("NOTIFICATION", r -> r
						.path("/api/notifications/**")
						.uri("lb://NOTIFICATION"))
				
				.route("REVIEW", r -> r
						.path("/api/reviews/**")
						.uri("lb://REVIEW"))
				
				.build();
	}
	
}

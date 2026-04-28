package com.bootcamp.gateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/gateway/user-service/**")
                        .filters(f -> f.rewritePath(
                                "/gateway/user-service/(?<segment>.*)",
                                "/user-service/${segment}"
                        ))
                        .uri("lb://user-service")
                )
                .route("pokemon-service", r -> r
                        .path("/gateway/pokemon-service/**")
                        .filters(f -> f.rewritePath(
                                "/gateway/pokemon-service/(?<segment>.*)",
                                "/pokemon-service/${segment}"
                        ))
                        .uri("lb://pokemon-service")
                )
                .route("trade-service", r -> r
                        .path("/gateway/trade-service/**")
                        .filters(f -> f.rewritePath(
                                "/gateway/trade-service/(?<segment>.*)",
                                "/trade-service/${segment}"
                        ))
                        .uri("lb://trade-service")
                )
                .build();
    }
}

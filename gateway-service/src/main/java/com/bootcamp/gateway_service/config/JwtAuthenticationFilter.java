package com.bootcamp.gateway_service.config;

import com.bootcamp.gateway_service.dto.response.BaseResponse;
import com.bootcamp.gateway_service.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JwtAuthenticationFilter implements WebFilter {

    public static final String[] PUBLIC_ENDPOINTS = {
            "/gateway/user-service/api/users/login",
            "/gateway/user-service/api/users/register"
    };

    public static final String USER_ID_HEADER = "X-Authenticated-User-Id";
    public static final String USER_EMAIL_HEADER = "X-Authenticated-User-Email";

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublicEndpoint(request)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        try {
            if (!jwtUtil.validateToken(token)) {
                return unauthorizedResponse(exchange, "Invalid or expired token");
            }

            String email = jwtUtil.extractEmail(token);
            Long userId = jwtUtil.extractUserId(token);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .headers(headers -> {
                        headers.set(USER_EMAIL_HEADER, email);
                        headers.set(USER_ID_HEADER, String.valueOf(userId));
                    })
                    .build();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    token,
                    java.util.Collections.emptyList()
            );

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        } catch (Exception exception) {
            log.debug("JWT authentication failed for path {}", request.getURI().getPath(), exception);
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }
    }

    public static boolean isPublicPath(String path) {
        return Arrays.stream(PUBLIC_ENDPOINTS).anyMatch(path::equals);
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        return HttpMethod.OPTIONS.equals(request.getMethod()) || isPublicPath(request.getURI().getPath());
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] responseBody = toJsonBytes(message);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody);
        return response.writeWith(Mono.just(buffer));
    }

    private byte[] toJsonBytes(String message) {
        try {
            return objectMapper.writeValueAsBytes(BaseResponse.error(message));
        } catch (JsonProcessingException exception) {
            log.warn("Failed to serialize unauthorized response body", exception);
            String fallbackResponse = "{\"status\":false,\"message\":\"" + message + "\",\"data\":null}";
            return fallbackResponse.getBytes(StandardCharsets.UTF_8);
        }
    }
}

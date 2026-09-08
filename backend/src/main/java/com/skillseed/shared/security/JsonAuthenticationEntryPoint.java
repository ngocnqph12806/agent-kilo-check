package com.skillseed.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillseed.shared.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns a stable 401 {@link ApiErrorResponse} JSON envelope whenever
 * an unauthenticated request hits a protected endpoint. Without this
 * bean, Spring Security's default sends a bare 403 for stateless
 * resource-server configs, which the FE cannot distinguish from a real
 * authorization failure.
 */
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JsonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiErrorResponse body = ApiErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHENTICATED",
                "Authentication required");
        objectMapper.writeValue(response.getWriter(), body);
    }
}

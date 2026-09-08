package com.skillseed.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillseed.shared.exception.ApiErrorResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import com.skillseed.auth.service.JwtService;

/**
 * Servlet filter that parses a {@code Bearer} access token from the request,
 * validates it via {@link JwtService}, and populates the
 * {@link org.springframework.security.core.context.SecurityContextHolder}
 * with an {@link AuthenticatedUser}.
 *
 * <p>Invalid/expired tokens produce a 401 JSON response written directly to
 * the response, bypassing downstream filters.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        try {
            Claims claims = jwtService.parseAndValidate(token, "access");
            UUID userId = UUID.fromString(claims.getSubject());
            String email = claims.get("email", String.class);
            Short level = claims.get("verificationLevel", Short.class);
            short verificationLevel = level == null ? 0 : level;

            AuthenticatedUser principal =
                    new AuthenticatedUser(userId, email, verificationLevel, true,
                            jwtService.parseRole(claims));

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
            writeUnauthorized(response, "INVALID_TOKEN", "Invalid or expired token");
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String code, String message)
            throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiErrorResponse body = ApiErrorResponse.of(401, code, message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
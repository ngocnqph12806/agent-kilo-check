package com.skillseed.auth.controller;

import com.skillseed.auth.dto.RegisterRequest;
import com.skillseed.auth.dto.SimpleMessageResponse;
import com.skillseed.auth.dto.VerifyEmailRequest;
import com.skillseed.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication endpoints: register, verify email, login, refresh,
 * logout, forgot/reset password, OAuth (Google/Apple).
 *
 * <p>All endpoints are mounted under {@code /api/v1/auth} and are public
 * (see {@code SecurityConfig#PUBLIC_PATHS}).
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Account creation, sign-in, and token management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new account with email + password")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered; verification email queued"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<SimpleMessageResponse> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleMessageResponse("Verification email sent"));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Confirm an email using the token sent at registration")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified"),
            @ApiResponse(responseCode = "400", description = "Token invalid or expired")
    })
    public ResponseEntity<SimpleMessageResponse> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest req) {
        authService.verifyEmail(req.token());
        return ResponseEntity.ok(new SimpleMessageResponse("Email verified"));
    }
}
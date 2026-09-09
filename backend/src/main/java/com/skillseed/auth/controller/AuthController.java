package com.skillseed.auth.controller;

import com.skillseed.auth.dto.AuthTokenResponse;
import com.skillseed.auth.dto.ForgotPasswordRequest;
import com.skillseed.auth.dto.LoginRequest;
import com.skillseed.auth.dto.OAuthAppleRequest;
import com.skillseed.auth.dto.OAuthGoogleRequest;
import com.skillseed.auth.dto.RefreshTokenRequest;
import com.skillseed.auth.dto.RegisterRequest;
import com.skillseed.auth.dto.RegisterResponse;
import com.skillseed.auth.dto.ResetPasswordRequest;
import com.skillseed.auth.dto.SimpleMessageResponse;
import com.skillseed.auth.dto.VerifyEmailRequest;
import com.skillseed.auth.dto.WalletChallengeRequest;
import com.skillseed.auth.dto.WalletChallengeResponse;
import com.skillseed.auth.dto.WalletVerifyRequest;
import com.skillseed.auth.security.RefreshTokenCookie;
import com.skillseed.auth.service.AuthService;
import com.skillseed.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * (see {@code SecurityConfig#PUBLIC_PATHS}). Login + refresh responses
 * also set a {@code HttpOnly} refresh-token cookie
 * (see {@link RefreshTokenCookie}) so hardened clients can avoid
 * sending the refresh token in the JSON body.
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
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        User user = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(authService.toSummary(user)));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Confirm an email using the token sent at registration")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified"),
            @ApiResponse(responseCode = "400", description = "Token invalid or expired")
    })
    public ResponseEntity<SimpleMessageResponse> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest req,
            @Parameter(hidden = true) HttpServletRequest httpRequest) {
        authService.verifyEmail(req.token(), clientKey(httpRequest));
        return ResponseEntity.ok(new SimpleMessageResponse("Email verified"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate with email + password; returns JWT pair")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "429", description = "Rate limited (5 attempts / 15 min / IP)")
    })
    public ResponseEntity<AuthTokenResponse> login(
            @Valid @RequestBody LoginRequest req,
            @Parameter(hidden = true) HttpServletRequest httpRequest,
            @Parameter(hidden = true) HttpServletResponse httpResponse) {
        String clientKey = clientKey(httpRequest);
        boolean secure = isSecure(httpRequest);
        AuthTokenResponse tokens = authService.login(req, clientKey, secure);
        RefreshTokenCookie.write(httpResponse, tokens.refreshToken(), secure);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new JWT pair")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token rotated"),
            @ApiResponse(responseCode = "401", description = "Refresh token invalid or expired")
    })
    public ResponseEntity<AuthTokenResponse> refresh(
            @Parameter(hidden = true) HttpServletRequest httpRequest,
            @Parameter(hidden = true) HttpServletResponse httpResponse,
            @RequestBody(required = false) RefreshTokenRequest req) {
        boolean secure = isSecure(httpRequest);
        AuthTokenResponse tokens = authService.refresh(httpRequest, req, secure);
        RefreshTokenCookie.write(httpResponse, tokens.refreshToken(), secure);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke the supplied refresh token (idempotent)")
    public ResponseEntity<SimpleMessageResponse> logout(
            @Parameter(hidden = true) HttpServletRequest httpRequest,
            @Parameter(hidden = true) HttpServletResponse httpResponse,
            @RequestBody(required = false) RefreshTokenRequest req) {
        authService.logout(httpRequest, req);
        RefreshTokenCookie.clear(httpResponse, isSecure(httpRequest));
        return ResponseEntity.ok(new SimpleMessageResponse("Logged out"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password-reset email (always returns success)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accepted (always)"),
            @ApiResponse(responseCode = "429",
                    description = "Too many attempts for this email (5 / hour)")
    })
    public ResponseEntity<SimpleMessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req,
            @Parameter(hidden = true) HttpServletRequest httpRequest) {
        authService.forgotPassword(req, clientKey(httpRequest));
        return ResponseEntity.ok(new SimpleMessageResponse(
                "If the email exists, a reset link has been sent"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset the password using a single-use token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated"),
            @ApiResponse(responseCode = "400", description = "Token invalid or expired")
    })
    public ResponseEntity<SimpleMessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok(new SimpleMessageResponse("Password updated"));
    }

    @PostMapping("/oauth/google")
    @Operation(summary = "Sign in or register with a Google id_token (T-M26)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated; JWT pair returned"),
            @ApiResponse(responseCode = "400", description = "id_token invalid or provider disabled")
    })
    public ResponseEntity<AuthTokenResponse> oauthGoogle(
            @Valid @RequestBody OAuthGoogleRequest req,
            @Parameter(hidden = true) HttpServletRequest httpRequest,
            @Parameter(hidden = true) HttpServletResponse httpResponse) {
        boolean secure = isSecure(httpRequest);
        AuthTokenResponse tokens = authService.loginWithGoogle(req, secure);
        RefreshTokenCookie.write(httpResponse, tokens.refreshToken(), secure);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/oauth/apple")
    @Operation(summary = "Sign in or register with an Apple id_token (T-M27, P1)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated; JWT pair returned"),
            @ApiResponse(responseCode = "400", description = "id_token invalid or provider disabled")
    })
    public ResponseEntity<AuthTokenResponse> oauthApple(
            @Valid @RequestBody OAuthAppleRequest req,
            @Parameter(hidden = true) HttpServletRequest httpRequest,
            @Parameter(hidden = true) HttpServletResponse httpResponse) {
        boolean secure = isSecure(httpRequest);
        AuthTokenResponse tokens = authService.loginWithApple(req, secure);
        RefreshTokenCookie.write(httpResponse, tokens.refreshToken(), secure);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/wallet/challenge")
    @Operation(summary = "Issue a fresh SIWE challenge message for the given wallet")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Challenge generated; sign and submit via /wallet/verify"),
            @ApiResponse(responseCode = "400", description = "Invalid address or unsupported chain id")
    })
    public ResponseEntity<WalletChallengeResponse> walletChallenge(
            @Valid @RequestBody WalletChallengeRequest req) {
        return ResponseEntity.ok(authService.walletChallenge(req));
    }

    @PostMapping("/wallet/verify")
    @Operation(summary = "Sign in (or register) with a verified SIWE signature")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated; JWT pair returned"),
            @ApiResponse(responseCode = "401", description = "Signature invalid, challenge replayed, or rate-limited")
    })
    public ResponseEntity<AuthTokenResponse> walletVerify(
            @Valid @RequestBody WalletVerifyRequest req,
            @Parameter(hidden = true) HttpServletRequest httpRequest,
            @Parameter(hidden = true) HttpServletResponse httpResponse) {
        boolean secure = isSecure(httpRequest);
        AuthTokenResponse tokens = authService.loginWithWallet(req, clientKey(httpRequest), secure);
        RefreshTokenCookie.write(httpResponse, tokens.refreshToken(), secure);
        return ResponseEntity.ok(tokens);
    }

    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return comma > 0 ? forwarded.substring(0, comma).trim() : forwarded.trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isSecure(HttpServletRequest request) {
        if (request.isSecure()) {
            return true;
        }
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return forwardedProto != null && forwardedProto.equalsIgnoreCase("https");
    }
}

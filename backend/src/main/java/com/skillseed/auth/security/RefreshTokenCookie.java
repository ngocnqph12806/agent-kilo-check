package com.skillseed.auth.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

/**
 * Centralises how the long-lived refresh token is written to and read
 * from the browser. The cookie is always {@code HttpOnly} (no JS
 * access), {@code SameSite=Lax} (cross-origin safe), and
 * {@code Secure} in production so it never traverses cleartext.
 *
 * <p>Used by {@code AuthController} on login / refresh / logout
 * responses, and by {@code AuthService.refresh} to read the token
 * out of the request when the client prefers not to send it in the
 * JSON body.
 */
public final class RefreshTokenCookie {

    public static final String NAME = "skillseed_refresh_token";
    public static final int MAX_AGE_SECONDS = 60 * 60 * 24 * 30;

    private RefreshTokenCookie() {
    }

    public static void write(HttpServletResponse response, String token, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(NAME, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(MAX_AGE_SECONDS)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void clear(HttpServletResponse response, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static String read(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie c : cookies) {
            if (NAME.equals(c.getName())) {
                String value = c.getValue();
                return value == null || value.isBlank() ? null : value;
            }
        }
        return null;
    }
}

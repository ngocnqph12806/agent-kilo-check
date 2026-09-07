package com.skillseed.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

/**
 * Convenience accessor for the {@link AuthenticatedUser} stored in the
 * Spring Security context for the current request.
 */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static AuthenticatedUser require() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof AuthenticatedUser u)) {
            throw new UsernameNotFoundException("No authenticated user");
        }
        return u;
    }

    public static UUID requireId() {
        return require().getUserId();
    }
}
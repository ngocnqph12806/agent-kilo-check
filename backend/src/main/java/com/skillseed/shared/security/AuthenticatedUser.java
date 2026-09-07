package com.skillseed.shared.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Lightweight {@link UserDetails} implementation populated from JWT claims.
 *
 * <p>Authorities are a single {@code ROLE_USER}. Account is considered
 * enabled when {@code verified=true}; downstream guards can require higher
 * verification levels if needed.
 */
public class AuthenticatedUser implements UserDetails {

    private final UUID userId;
    private final String email;
    private final short verificationLevel;
    private final boolean verified;

    public AuthenticatedUser(UUID userId, String email, short verificationLevel, boolean verified) {
        this.userId = userId;
        this.email = email;
        this.verificationLevel = verificationLevel;
        this.verified = verified;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public short getVerificationLevel() {
        return verificationLevel;
    }

    public boolean isVerified() {
        return verified;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return verified;
    }
}
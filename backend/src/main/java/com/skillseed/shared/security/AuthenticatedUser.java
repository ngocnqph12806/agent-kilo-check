package com.skillseed.shared.security;

import com.skillseed.shared.domain.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Lightweight {@link UserDetails} implementation populated from JWT claims.
 *
 * <p>Authorities expand to {@code ROLE_USER} or {@code ROLE_USER + ROLE_ADMIN}
 * depending on the {@code role} claim. Account is considered enabled when
 * {@code verified=true}; downstream guards can require higher verification
 * levels if needed.
 */
public class AuthenticatedUser implements UserDetails {

    private final UUID userId;
    private final String email;
    private final short verificationLevel;
    private final boolean verified;
    private final Role role;

    public AuthenticatedUser(UUID userId, String email, short verificationLevel,
                             boolean verified, Role role) {
        this.userId = userId;
        this.email = email;
        this.verificationLevel = verificationLevel;
        this.verified = verified;
        this.role = role == null ? Role.USER : role;
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

    public Role getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isAdmin()) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_USER"),
                    new SimpleGrantedAuthority("ROLE_ADMIN")
            );
        }
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

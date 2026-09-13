package com.skillseed.wallet.exception;

import com.skillseed.shared.exception.DomainException;
import org.springframework.http.HttpStatus;

/**
 * Wallet module domain exception.
 *
 * <p>Retained as a thin wrapper around {@link DomainException} for
 * backwards compatibility and to keep {@code throws} clauses expressive
 * (T-M411). Prefer throwing {@link DomainException} directly in new code.
 *
 * @deprecated since Sprint 6 — extend {@link DomainException} directly.
 */
@Deprecated
public class WalletException extends DomainException {

    public WalletException(String code, String message, HttpStatus status) {
        super(code, message, status);
    }

    public static WalletException badRequest(String code, String message) {
        return new WalletException(code, message, HttpStatus.BAD_REQUEST);
    }

    public static WalletException notFound(String code, String message) {
        return new WalletException(code, message, HttpStatus.NOT_FOUND);
    }

    public static WalletException conflict(String code, String message) {
        return new WalletException(code, message, HttpStatus.CONFLICT);
    }
}

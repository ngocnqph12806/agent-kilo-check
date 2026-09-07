package com.skillseed.wallet.exception;

import org.springframework.http.HttpStatus;

public class WalletException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public WalletException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getHttpStatus() {
        return status.value();
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
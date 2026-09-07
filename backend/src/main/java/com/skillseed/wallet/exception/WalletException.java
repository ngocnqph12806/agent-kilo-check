package com.skillseed.wallet.exception;

public class WalletException extends RuntimeException {

    private final String code;
    private final int httpStatus;

    public WalletException(String code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public static WalletException badRequest(String code, String message) {
        return new WalletException(code, message, 400);
    }

    public static WalletException notFound(String code, String message) {
        return new WalletException(code, message, 404);
    }
}
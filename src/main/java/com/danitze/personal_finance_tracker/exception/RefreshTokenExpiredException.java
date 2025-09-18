package com.danitze.personal_finance_tracker.exception;

public class RefreshTokenExpiredException extends RuntimeException {

    public RefreshTokenExpiredException() {
        super("Refresh token expired");
    }

}

package com.danitze.personal_finance_tracker.exception.auth;

public class RefreshTokenExpiredException extends RuntimeException {

    public RefreshTokenExpiredException() {
        super("Refresh token expired");
    }

}

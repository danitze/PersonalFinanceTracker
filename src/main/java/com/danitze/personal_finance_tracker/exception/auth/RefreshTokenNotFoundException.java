package com.danitze.personal_finance_tracker.exception.auth;

public class RefreshTokenNotFoundException extends RuntimeException {

    public RefreshTokenNotFoundException() {
        super("Refresh token not found");
    }

}

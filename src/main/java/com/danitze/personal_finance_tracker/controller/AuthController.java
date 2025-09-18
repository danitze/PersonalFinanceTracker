package com.danitze.personal_finance_tracker.controller;

import com.danitze.personal_finance_tracker.dto.*;
import com.danitze.personal_finance_tracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody SignUpDto signUpDto
    ) {
        authService.signUp(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(
            @Valid @RequestBody LogInDto logInDto
    ) {
        return ResponseEntity.ok(authService.logIn(logInDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenDto> refreshAccessToken(
            @Valid @RequestBody RefreshAccessTokenDto refreshAccessTokenDto
    ) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshAccessTokenDto));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogOutDto logOutDto
    ) {
        authService.logout(logOutDto);
        return ResponseEntity.noContent().build();
    }

}

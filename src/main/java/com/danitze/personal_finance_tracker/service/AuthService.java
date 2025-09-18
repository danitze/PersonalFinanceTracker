package com.danitze.personal_finance_tracker.service;

import com.danitze.personal_finance_tracker.dto.*;
import com.danitze.personal_finance_tracker.entity.RefreshToken;
import com.danitze.personal_finance_tracker.entity.User;
import com.danitze.personal_finance_tracker.entity.UserRole;
import com.danitze.personal_finance_tracker.entity.enums.Role;
import com.danitze.personal_finance_tracker.exception.*;
import com.danitze.personal_finance_tracker.repository.RefreshTokenRepository;
import com.danitze.personal_finance_tracker.repository.UserRepository;
import com.danitze.personal_finance_tracker.repository.UserRoleRepository;
import com.danitze.personal_finance_tracker.security.JwtUtil;
import com.danitze.personal_finance_tracker.security.SecurityUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void signUp(SignUpDto signUpDto) {
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new EmailAlreadyUsedException("Email already used");
        }
        String passwordHash = passwordEncoder.encode(signUpDto.getPassword());
        UserRole userRole = userRoleRepository.findByRole(Role.USER)
                .orElseThrow(() -> new UserRoleNotFoundException(Role.USER));
        User user = User.builder()
                .email(signUpDto.getEmail())
                .passwordHash(passwordHash)
                .roles(Set.of(userRole))
                .build();
        userRepository.save(user);
    }

    @Transactional
    public TokenDto logIn(LogInDto logInDto) {
        User user = userRepository.findByEmail(logInDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(logInDto.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        UserDetails userDetails = new SecurityUser(user);
        String token = jwtUtil.generateToken(userDetails);
        refreshTokenRepository.deleteByUser(user);
        String refreshTokenJwt = jwtUtil.generateRefreshToken(userDetails);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenJwt)
                .expirationDate(
                        jwtUtil.getExpirationDate(refreshTokenJwt)
                                .toInstant().atOffset(ZoneOffset.UTC)
                )
                .build();
        refreshToken = refreshTokenRepository.save(refreshToken);
        return new TokenDto(token, refreshToken.getToken());
    }

    @Transactional
    public TokenDto refreshAccessToken(RefreshAccessTokenDto refreshAccessTokenDto) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshAccessTokenDto.getRefreshToken())
                .orElseThrow(RefreshTokenNotFoundException::new);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (now.isAfter(refreshToken.getExpirationDate())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException();
        }

        User user = refreshToken.getUser();
        UserDetails userDetails = new SecurityUser(user);
        if (!jwtUtil.validateToken(refreshToken.getToken(), userDetails)) {
            throw new BadCredentialsException("Invalid token");
        }

        refreshTokenRepository.delete(refreshToken);
        String refreshTokenJwt = jwtUtil.generateRefreshToken(userDetails);
        refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenJwt)
                .expirationDate(
                        jwtUtil.getExpirationDate(refreshTokenJwt)
                                .toInstant().atOffset(ZoneOffset.UTC)
                )
                .build();
        refreshToken = refreshTokenRepository.save(refreshToken);
        String token = jwtUtil.generateToken(userDetails);
        return new TokenDto(token, refreshToken.getToken());
    }

    @Transactional
    public void logout(LogOutDto logOutDto) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(logOutDto.getRefreshToken())
                .orElseThrow(RefreshTokenNotFoundException::new);
        refreshTokenRepository.delete(refreshToken);
    }

}

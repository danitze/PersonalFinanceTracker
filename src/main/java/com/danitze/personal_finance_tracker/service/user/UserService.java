package com.danitze.personal_finance_tracker.service.user;

import com.danitze.personal_finance_tracker.dto.user.CreateUserDto;
import com.danitze.personal_finance_tracker.dto.user.UpdateUserDto;
import com.danitze.personal_finance_tracker.dto.user.UserDto;
import com.danitze.personal_finance_tracker.dto.user.UserMapper;
import com.danitze.personal_finance_tracker.entity.User;
import com.danitze.personal_finance_tracker.entity.UserRole;
import com.danitze.personal_finance_tracker.exception.auth.EmailAlreadyUsedException;
import com.danitze.personal_finance_tracker.exception.user.UserNotFoundException;
import com.danitze.personal_finance_tracker.exception.user.UserRoleNotFoundException;
import com.danitze.personal_finance_tracker.repository.UserRepository;
import com.danitze.personal_finance_tracker.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new EmailAlreadyUsedException("Email already used");
        }
        String passwordHash = passwordEncoder.encode(createUserDto.getPassword());
        Set<UserRole> userRoles = userRoleRepository.findAllByRoleIn(createUserDto.getRoles());
        createUserDto.getRoles().stream()
                .filter(role -> userRoles.stream().noneMatch(userRole -> userRole.getRole() == role))
                .findFirst()
                .ifPresent(missingRole -> { throw new UserRoleNotFoundException(missingRole); });
        User user = User.builder()
                .email(createUserDto.getEmail())
                .passwordHash(passwordHash)
                .roles(userRoles)
                .build();
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User with " + email + " not found"));
    }

    public UserDto getUser(Long id) {
        return userRepository.findById(id).map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User with " + id + " not found"));
    }

    public Page<UserDto> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with " + id + " not found"));
        userRepository.delete(user);
    }

    public UserDto updateUser(Long id, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with " + id + " not found"));
        user.setPasswordHash(passwordEncoder.encode(updateUserDto.getPassword()));
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    public boolean isOwner(Long userId, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with " + userId + " not found"));
        return user.getEmail().equals(email);
    }

}

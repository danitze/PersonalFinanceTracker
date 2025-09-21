package com.danitze.personal_finance_tracker.controller;

import com.danitze.personal_finance_tracker.dto.account.AccountDto;
import com.danitze.personal_finance_tracker.dto.account.CreateAccountDto;
import com.danitze.personal_finance_tracker.dto.user.CreateUserDto;
import com.danitze.personal_finance_tracker.dto.user.UpdateUserDto;
import com.danitze.personal_finance_tracker.dto.user.UserDto;
import com.danitze.personal_finance_tracker.service.account.AccountService;
import com.danitze.personal_finance_tracker.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public UserController(
            UserService userService,
            AccountService accountService
    ) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        UserDto userDto = userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isOwner(#id, principal.username)")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping("/{id}/accounts")
    @PreAuthorize("hasRole('ADMIN') or @userService.isOwner(#id, principal.username)")
    public ResponseEntity<AccountDto> createAccount(
            @PathVariable Long id,
            @Valid @RequestBody CreateAccountDto createAccountDto
    ) {
        AccountDto accountDto = accountService.createAccount(id, createAccountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountDto);
    }

    @GetMapping("/{id}/accounts")
    @PreAuthorize("hasRole('ADMIN') or @userService.isOwner(#id, principal.username)")
    public ResponseEntity<Page<AccountDto>> getAccounts(
            @PathVariable Long id,
            @RequestParam(value = "limit", defaultValue = "30") int limit,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(accountService.getAccounts(id, pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(value = "limit", defaultValue = "30") int limit,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(userService.getUsers(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isOwner(#id, principal.username)")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserDto));
    }
}

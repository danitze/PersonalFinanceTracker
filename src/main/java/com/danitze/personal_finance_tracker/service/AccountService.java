package com.danitze.personal_finance_tracker.service;

import com.danitze.personal_finance_tracker.dto.AccountDto;
import com.danitze.personal_finance_tracker.dto.AccountMapper;
import com.danitze.personal_finance_tracker.dto.CreateAccountDto;
import com.danitze.personal_finance_tracker.dto.UpdateAccountDto;
import com.danitze.personal_finance_tracker.entity.Account;
import com.danitze.personal_finance_tracker.entity.User;
import com.danitze.personal_finance_tracker.exception.AccountNotFoundException;
import com.danitze.personal_finance_tracker.exception.UserNotFoundException;
import com.danitze.personal_finance_tracker.repository.AccountRepository;
import com.danitze.personal_finance_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private final AccountMapper accountMapper;

    @Autowired
    public AccountService(
            AccountRepository accountRepository,
            UserRepository userRepository,
            AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountMapper = accountMapper;
    }

    public AccountDto createAccount(
            Long userId,
            CreateAccountDto createAccountDto
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Account account = Account.builder()
                .user(user)
                .name(createAccountDto.getName())
                .currency(createAccountDto.getCurrency())
                .balance(createAccountDto.getBalance())
                .build();
        account = accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    public Page<AccountDto> getAccounts(
            Long userId,
            Pageable pageable
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return accountRepository.findByUser(user, pageable)
                .map(accountMapper::toDto);
    }

    public AccountDto getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return accountMapper.toDto(account);
    }

    public AccountDto updateAccount(Long id, UpdateAccountDto updateAccountDto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        account.setName(updateAccountDto.getName());
        account.setCurrency(updateAccountDto.getCurrency());
        return accountMapper.toDto(accountRepository.save(account));
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        accountRepository.delete(account);
    }

    public boolean isOwner(Long accountId, String email) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return account.getUser().getEmail().equals(email);
    }

}

package com.danitze.personal_finance_tracker.service;

import com.danitze.personal_finance_tracker.dto.BudgetLimitDto;
import com.danitze.personal_finance_tracker.dto.BudgetLimitMapper;
import com.danitze.personal_finance_tracker.dto.CreateBudgetLimitDto;
import com.danitze.personal_finance_tracker.dto.UpdateBudgetLimitDto;
import com.danitze.personal_finance_tracker.entity.Account;
import com.danitze.personal_finance_tracker.entity.BudgetLimit;
import com.danitze.personal_finance_tracker.exception.AccountNotFoundException;
import com.danitze.personal_finance_tracker.exception.BudgetLimitAlreadyUsedException;
import com.danitze.personal_finance_tracker.exception.BudgetLimitNotFoundException;
import com.danitze.personal_finance_tracker.repository.AccountRepository;
import com.danitze.personal_finance_tracker.repository.BudgetLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetLimitService {

    private final BudgetLimitRepository budgetLimitRepository;
    private final AccountRepository accountRepository;

    private final BudgetLimitMapper budgetLimitMapper;

    @Autowired
    public BudgetLimitService(
            BudgetLimitRepository budgetLimitRepository,
            AccountRepository accountRepository,
            BudgetLimitMapper budgetLimitMapper
    ) {
        this.budgetLimitRepository = budgetLimitRepository;
        this.accountRepository = accountRepository;
        this.budgetLimitMapper = budgetLimitMapper;
    }

    public BudgetLimitDto createBudgetLimit(
            Long accountId,
            CreateBudgetLimitDto createBudgetLimitDto
    ) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        if (budgetLimitRepository.existsByAccountAndCategory(account, createBudgetLimitDto.getCategory())) {
            throw new BudgetLimitAlreadyUsedException(createBudgetLimitDto.getCategory());
        }
        BudgetLimit budgetLimit = BudgetLimit.builder()
                .account(account)
                .category(createBudgetLimitDto.getCategory())
                .amount(createBudgetLimitDto.getAmount())
                .build();
        return budgetLimitMapper.toDto(budgetLimitRepository.save(budgetLimit));
    }

    public List<BudgetLimitDto> getBudgetLimits(
            Long accountId
    ) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return budgetLimitRepository.findAllByAccount(account).stream()
                .map(budgetLimitMapper::toDto)
                .collect(Collectors.toList());
    }

    public BudgetLimitDto updateBudgetLimit(
            Long budgetLimitId,
            UpdateBudgetLimitDto updateBudgetLimitDto
    ) {
        BudgetLimit budgetLimit = budgetLimitRepository.findById(budgetLimitId)
                .orElseThrow(BudgetLimitNotFoundException::new);
        budgetLimit.setAmount(updateBudgetLimitDto.getAmount());
        return budgetLimitMapper.toDto(budgetLimitRepository.save(budgetLimit));
    }

    public void deleteBudgetLimit(
            Long budgetLimitId
    ) {
        BudgetLimit budgetLimit = budgetLimitRepository.findById(budgetLimitId)
                .orElseThrow(BudgetLimitNotFoundException::new);
        budgetLimitRepository.delete(budgetLimit);
    }

    public boolean isOwner(Long budgetLimitId, String email) {
        BudgetLimit budgetLimit = budgetLimitRepository.findById(budgetLimitId)
                .orElseThrow(BudgetLimitNotFoundException::new);
        return budgetLimit.getAccount().getUser().getEmail().equals(email);
    }


}

package com.danitze.personal_finance_tracker.service;

import com.danitze.personal_finance_tracker.dto.*;
import com.danitze.personal_finance_tracker.entity.Account;
import com.danitze.personal_finance_tracker.entity.Transaction;
import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import com.danitze.personal_finance_tracker.entity.enums.TransactionType;
import com.danitze.personal_finance_tracker.exception.AccountNotFoundException;
import com.danitze.personal_finance_tracker.exception.TransactionNotFoundException;
import com.danitze.personal_finance_tracker.repository.AccountRepository;
import com.danitze.personal_finance_tracker.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            TransactionMapper transactionMapper
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public TransactionDto createTransaction(
            Long accountId,
            CreateTransactionDto createTransactionDto
    ) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        Transaction transaction = Transaction.builder()
                .account(account)
                .category(createTransactionDto.getCategory())
                .amount(createTransactionDto.getAmount())
                .type(createTransactionDto.getType())
                .txDate(createTransactionDto.getTxDate())
                .note(
                        Optional.ofNullable(createTransactionDto.getNote())
                                .filter(s -> !s.isBlank())
                                .orElse(null)
                )
                .build();
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        transaction = transactionRepository.saveAndFlush(transaction);
        return transactionMapper.toDto(transaction);
    }

    public Page<TransactionDto> getTransactions(Long accountId, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return transactionRepository.findByAccount(account, pageable)
                .map(transactionMapper::toDto);
    }

    public TransactionDto getTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        return transactionMapper.toDto(transaction);
    }

    public Page<TransactionDto> getTransactionsFilteredByDate(
            Long accountId,
            OffsetDateTime from,
            OffsetDateTime to,
            Pageable pageable
    ) {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("from must be before to");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return transactionRepository.findByAccountAndTxDateBetween(
                account,
                from,
                to,
                pageable
        ).map(transactionMapper::toDto);
    }

    public List<TransactionCategorySummaryDto> getCategoriesSummaries(
            Long accountId,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("from must be before to");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return transactionRepository.findCategoriesSummaries(account, from, to);
    }

    public TransactionsSummaryDto getTransactionsSummary(
            Long accountId,
            OffsetDateTime from,
            OffsetDateTime to,
            @Nullable Set<TransactionType> transactionTypes,
            @Nullable Set<TransactionCategory> transactionCategories
    ) {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("from must be before to");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return transactionRepository.getTransactionsSummary(account, from, to, transactionTypes, transactionCategories);
    }

    public boolean isOwner(Long transactionId, String email) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        return transaction.getAccount().getUser().getEmail().equals(email);
    }

}

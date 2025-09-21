package com.danitze.personal_finance_tracker.service.transaction;

import com.danitze.personal_finance_tracker.dto.notification.CreateNotificationDto;
import com.danitze.personal_finance_tracker.dto.transaction.*;
import com.danitze.personal_finance_tracker.entity.Account;
import com.danitze.personal_finance_tracker.entity.BudgetLimit;
import com.danitze.personal_finance_tracker.entity.Transaction;
import com.danitze.personal_finance_tracker.entity.enums.*;
import com.danitze.personal_finance_tracker.exception.account.AccountNotFoundException;
import com.danitze.personal_finance_tracker.exception.transaction.TransactionNotFoundException;
import com.danitze.personal_finance_tracker.repository.AccountRepository;
import com.danitze.personal_finance_tracker.repository.TransactionRepository;
import com.danitze.personal_finance_tracker.service.budget.BudgetLimitService;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BudgetLimitService budgetLimitService;
    private final TransactionNotificationSenderService transactionNotificationSenderService;

    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            BudgetLimitService budgetLimitService,
            TransactionNotificationSenderService transactionNotificationSenderService,
            TransactionMapper transactionMapper
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.budgetLimitService = budgetLimitService;
        this.transactionNotificationSenderService = transactionNotificationSenderService;
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
        Optional<BudgetThreshold> budgetThresholdOptional = needSendLimitNotification(accountId, transaction);
        if (budgetThresholdOptional.isPresent()) {
            BudgetThreshold budgetThreshold = budgetThresholdOptional.get();
            String message = switch (budgetThreshold) {
                case WARNING_80 -> "Warning! 80% limit for category " + transaction.getCategory() + " reached";
                case LIMIT_100 ->  "Limit for the category " + transaction.getCategory() + " reached";
            };
            CreateNotificationDto createNotificationDto = CreateNotificationDto.builder()
                    .type(NotificationType.LOW_BALANCE)
                    .message(message)
                    .channel(NotificationChannel.EMAIL)
                    .build();
            transactionNotificationSenderService.sendTransactionNotification(accountId, createNotificationDto);
        }
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

    private Optional<BudgetThreshold> needSendLimitNotification(
            Long accountId,
            Transaction transaction
    ) {
        List<BudgetLimit> budgetLimits = budgetLimitService.getBudgetLimitsEntities(accountId);
        BudgetLimit budgetLimit = budgetLimits.stream()
                .filter(limit -> limit.getCategory() == transaction.getCategory())
                .findFirst()
                .orElse(null);
        if (budgetLimit == null) {
            return Optional.empty();
        }
        List<TransactionCategorySummaryDto> summaries = getCategoriesSummaries(
                accountId,
                OffsetDateTime.now().minusMonths(1L),
                OffsetDateTime.now()
        );
        BigDecimal amountPreviouslySpent = summaries.stream()
                .filter(s -> s.getTransactionCategory() == transaction.getCategory())
                .map(TransactionCategorySummaryDto::getTotalAmount)
                .findFirst()
                .orElse(BigDecimal.ZERO);
        BigDecimal absAmountPreviouslySpent = amountPreviouslySpent.abs();
        BigDecimal amountSpent = amountPreviouslySpent
                .add(transaction.getAmount())
                .abs();
        BigDecimal limitThreshold = budgetLimit.getAmount();
        BigDecimal warningThreshold = budgetLimit.getAmount().multiply(BigDecimal.valueOf(0.8));
        if (amountSpent.compareTo(limitThreshold) > 0) {
            if (absAmountPreviouslySpent.compareTo(limitThreshold) > 0) {
                return Optional.empty();
            } else {
                return Optional.of(BudgetThreshold.LIMIT_100);
            }
        } else if (amountSpent.compareTo(warningThreshold) > 0) {
            if (absAmountPreviouslySpent.compareTo(warningThreshold) > 0) {
                return Optional.empty();
            } else {
                return Optional.of(BudgetThreshold.WARNING_80);
            }
        } else {
            return Optional.empty();
        }
    }

}

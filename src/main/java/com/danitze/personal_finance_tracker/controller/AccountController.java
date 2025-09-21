package com.danitze.personal_finance_tracker.controller;

import com.danitze.personal_finance_tracker.dto.account.AccountDto;
import com.danitze.personal_finance_tracker.dto.account.UpdateAccountDto;
import com.danitze.personal_finance_tracker.dto.budget.BudgetLimitDto;
import com.danitze.personal_finance_tracker.dto.budget.CreateBudgetLimitDto;
import com.danitze.personal_finance_tracker.dto.notification.CreateNotificationDto;
import com.danitze.personal_finance_tracker.dto.notification.NotificationDto;
import com.danitze.personal_finance_tracker.dto.transaction.CreateTransactionDto;
import com.danitze.personal_finance_tracker.dto.transaction.TransactionCategorySummaryDto;
import com.danitze.personal_finance_tracker.dto.transaction.TransactionDto;
import com.danitze.personal_finance_tracker.dto.transaction.TransactionsSummaryDto;
import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import com.danitze.personal_finance_tracker.entity.enums.TransactionType;
import com.danitze.personal_finance_tracker.service.account.AccountService;
import com.danitze.personal_finance_tracker.service.budget.BudgetLimitService;
import com.danitze.personal_finance_tracker.service.notification.NotificationService;
import com.danitze.personal_finance_tracker.service.transaction.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BudgetLimitService budgetLimitService;
    private final NotificationService notificationService;


    @Autowired
    public AccountController(
            AccountService accountService,
            TransactionService transactionService,
            BudgetLimitService budgetLimitService,
            NotificationService notificationService
    ) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.budgetLimitService = budgetLimitService;
        this.notificationService = notificationService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id, principal.username)")
    public ResponseEntity<AccountDto> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id, principal.username)")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccountDto updateAccountDto
    ) {
        AccountDto accountDto = accountService.updateAccount(id, updateAccountDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(accountDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id, principal.username)")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/transactions")
    @PreAuthorize("@accountService.isOwner(#id, principal.username)")
    public ResponseEntity<TransactionDto> createTransaction(
            @PathVariable Long id,
            @Valid @RequestBody CreateTransactionDto createTransactionDto
    ) {
        TransactionDto transactionDto = transactionService.createTransaction(id, createTransactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionDto);
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id, principal.username)")
    public ResponseEntity<Page<TransactionDto>> getTransactions(
            @PathVariable Long id,
            @RequestParam(value = "limit", defaultValue = "30") int limit,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sort[txDate]", required = false) Optional<Sort.Direction> txDateSort,
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @RequestParam(value = "to", required = false) OffsetDateTime to
    ) {
        List<Sort.Order> orders = new ArrayList<>();
        txDateSort.ifPresent(direction -> orders.add(Sort.Order.by("txDate").with(direction)));
        if (orders.isEmpty()) {
            orders.add(Sort.Order.by("txDate").with(Sort.Direction.DESC));
        }
        Pageable pageable = PageRequest.of(
                page,
                limit,
                Sort.by(orders)
        );
        if (from != null && to != null) {
            return ResponseEntity.ok(transactionService.getTransactionsFilteredByDate(id, from, to, pageable));
        }
        return ResponseEntity.ok(transactionService.getTransactions(id, pageable));
    }

    @GetMapping("/{id}/transactions-categories-summaries")
    @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id, principal.username)")
    public ResponseEntity<List<TransactionCategorySummaryDto>> getTransactionCategoriesSummaries(
            @PathVariable Long id,
            @RequestParam(value = "from") OffsetDateTime from,
            @RequestParam(value = "to") OffsetDateTime to
    ) {
        return ResponseEntity.ok(transactionService.getCategoriesSummaries(id, from, to));
    }

    @GetMapping("/{id}/transactions-summaries")
    @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id, principal.username)")
    public ResponseEntity<TransactionsSummaryDto> getTransactionsSummaries(
            @PathVariable Long id,
            @RequestParam(value = "from") OffsetDateTime from,
            @RequestParam(value = "to") OffsetDateTime to,
            @RequestParam(value = "transaction_types", required = false) Set<TransactionType> transactionTypes,
            @RequestParam(value = "transaction_categories", required = false) Set<TransactionCategory> transactionCategories
    ) {
        return ResponseEntity.ok(
                transactionService.getTransactionsSummary(
                        id,
                        from,
                        to,
                        transactionTypes,
                        transactionCategories
                )
        );
    }

    @PostMapping("/{id}/budget-limits")
    @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id, principal.username)")
    public ResponseEntity<BudgetLimitDto> createBudgetLimit(
            @PathVariable Long id,
            @Valid @RequestBody CreateBudgetLimitDto createBudgetLimitDto
    ) {
        BudgetLimitDto budgetLimitDto = budgetLimitService.createBudgetLimit(id, createBudgetLimitDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetLimitDto);
    }

    @GetMapping("/{id}/budget-limits")
    @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id, principal.username)")
    public ResponseEntity<List<BudgetLimitDto>> getBudgetLimits(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(budgetLimitService.getBudgetLimits(id));
    }

    @PostMapping("/{id}/notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationDto> createNotification(
            @PathVariable Long id,
            @Valid @RequestBody CreateNotificationDto createNotificationDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationService.createNotification(id, createNotificationDto));
    }

    @GetMapping("/{id}/notifications")
    @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id, principal.username)")
    public ResponseEntity<Page<NotificationDto>> getNotifications(
            @PathVariable Long id,
            @RequestParam(value = "limit", defaultValue = "30") int limit,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "not_read_only", defaultValue = "false") boolean notReadOnly
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(notificationService.getNotifications(id, notReadOnly, pageable));
    }

    @GetMapping("/{id}/notifications/unread")
    @PreAuthorize("hasRole('ADMIN') or @accountService.isOwner(#id, principal.username)")
    public ResponseEntity<List<NotificationDto>> getAllUnreadNotifications(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(notificationService.getAllUnreadNotifications(id));
    }
}

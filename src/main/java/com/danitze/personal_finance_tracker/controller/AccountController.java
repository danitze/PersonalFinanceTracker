package com.danitze.personal_finance_tracker.controller;

import com.danitze.personal_finance_tracker.dto.*;
import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import com.danitze.personal_finance_tracker.entity.enums.TransactionType;
import com.danitze.personal_finance_tracker.service.AccountService;
import com.danitze.personal_finance_tracker.service.BudgetLimitService;
import com.danitze.personal_finance_tracker.service.TransactionService;
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


    @Autowired
    public AccountController(
            AccountService accountService,
            TransactionService transactionService,
            BudgetLimitService budgetLimitService
    ) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.budgetLimitService = budgetLimitService;
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
}

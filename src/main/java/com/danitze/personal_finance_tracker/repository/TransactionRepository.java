package com.danitze.personal_finance_tracker.repository;

import com.danitze.personal_finance_tracker.entity.Account;
import com.danitze.personal_finance_tracker.entity.Transaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByAccount(@NotNull Account account, @NotNull Pageable pageable);

    Page<Transaction> findByAccountAndTxDateBetween(
            @NotNull Account account,
            @NotNull OffsetDateTime from,
            @NotNull OffsetDateTime to,
            @NotNull Pageable pageable
    );

}

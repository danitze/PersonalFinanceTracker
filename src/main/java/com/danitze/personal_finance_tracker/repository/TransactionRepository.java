package com.danitze.personal_finance_tracker.repository;

import com.danitze.personal_finance_tracker.dto.transaction.TransactionCategorySummaryDto;
import com.danitze.personal_finance_tracker.dto.transaction.TransactionsSummaryDto;
import com.danitze.personal_finance_tracker.entity.Account;
import com.danitze.personal_finance_tracker.entity.Transaction;
import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import com.danitze.personal_finance_tracker.entity.enums.TransactionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByAccount(@NotNull Account account, @NotNull Pageable pageable);

    Page<Transaction> findByAccountAndTxDateBetween(
            @NotNull Account account,
            @NotNull OffsetDateTime from,
            @NotNull OffsetDateTime to,
            @NotNull Pageable pageable
    );

    @Query("""
            SELECT new com.danitze.personal_finance_tracker.dto.transaction.TransactionCategorySummaryDto(t.category, SUM(t.amount))
            FROM Transaction t
            WHERE t.account = :account
            AND t.txDate BETWEEN :from AND :to
            GROUP BY t.category
            """)
    List<TransactionCategorySummaryDto> findCategoriesSummaries(
            @Param("account") Account account,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    @Query("""
            SELECT new com.danitze.personal_finance_tracker.dto.transaction.TransactionsSummaryDto(
                        SUM(CASE WHEN t.amount > 0 THEN t.amount ELSE 0 END),
                        SUM(CASE WHEN t.amount < 0 THEN t.amount ELSE 0 END)
            )
            FROM Transaction t
            WHERE t.account = :account
            AND t.txDate BETWEEN :from AND :to
            AND (:transactionTypes IS NULL OR t.type IN :transactionTypes)
            AND (:transactionCategories IS NULL or t.category IN :transactionCategories)
            """)
    TransactionsSummaryDto getTransactionsSummary(
            @Param("account") Account account,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Nullable @Param("transactionTypes") Set<TransactionType> transactionTypes,
            @Nullable @Param("transactionCategories") Set<TransactionCategory> transactionCategories
    );

}

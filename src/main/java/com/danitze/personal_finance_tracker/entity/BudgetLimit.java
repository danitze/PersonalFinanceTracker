package com.danitze.personal_finance_tracker.entity;

import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "budget_limits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "budget_limit_seq")
    @SequenceGenerator(name = "budget_limit_seq", sequenceName = "budget_limit_sequence", allocationSize = 1)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @Column(name = "category", nullable = false)
    private TransactionCategory category;

    @NotNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

}

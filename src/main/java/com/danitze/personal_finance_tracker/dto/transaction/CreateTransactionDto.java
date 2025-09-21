package com.danitze.personal_finance_tracker.dto.transaction;

import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import com.danitze.personal_finance_tracker.entity.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionDto {
    @NotNull
    private TransactionCategory category;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    @NotNull
    @JsonProperty("tx_date")
    private OffsetDateTime txDate;

    private String note;

    @AssertTrue(message = "Amount must be positive or negative, not zero")
    public boolean isNonZero() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) != 0;
    }

    @AssertTrue(message = "Amount sign should be correct for your category")
    public boolean checkAmountSign() {
        return switch (type) {
            case INCOME -> amount.signum() == 1;
            case EXPENSE -> amount.signum() == -1;
        };
    }
}

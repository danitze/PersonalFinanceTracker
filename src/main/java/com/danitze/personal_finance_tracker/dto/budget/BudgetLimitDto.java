package com.danitze.personal_finance_tracker.dto.budget;

import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetLimitDto {

    @NonNull
    private Long id;

    @NonNull
    private TransactionCategory category;

    @NonNull
    private BigDecimal amount;

}

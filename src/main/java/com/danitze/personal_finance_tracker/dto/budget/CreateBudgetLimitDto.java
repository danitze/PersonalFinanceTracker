package com.danitze.personal_finance_tracker.dto.budget;

import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBudgetLimitDto {

    @NotNull
    private TransactionCategory category;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Positive
    private BigDecimal amount;

}

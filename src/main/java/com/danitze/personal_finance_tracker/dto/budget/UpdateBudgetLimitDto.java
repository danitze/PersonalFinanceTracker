package com.danitze.personal_finance_tracker.dto.budget;

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
public class UpdateBudgetLimitDto {

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Positive
    private BigDecimal amount;

}

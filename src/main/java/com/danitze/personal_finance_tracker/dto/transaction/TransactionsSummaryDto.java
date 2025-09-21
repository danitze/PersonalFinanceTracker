package com.danitze.personal_finance_tracker.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsSummaryDto {

    @NonNull
    @JsonProperty("total_income")
    private BigDecimal totalIncome;

    @NonNull
    @JsonProperty("total_expense")
    private BigDecimal totalExpense;

}

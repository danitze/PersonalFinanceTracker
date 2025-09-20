package com.danitze.personal_finance_tracker.dto;

import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCategorySummaryDto {

    @NonNull
    private TransactionCategory transactionCategory;

    @NonNull
    private BigDecimal totalAmount;

}

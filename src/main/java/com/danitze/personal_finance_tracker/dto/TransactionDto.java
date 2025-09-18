package com.danitze.personal_finance_tracker.dto;

import com.danitze.personal_finance_tracker.entity.enums.TransactionCategory;
import com.danitze.personal_finance_tracker.entity.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    @NonNull
    private Long id;

    @NonNull
    @JsonProperty("account_id")
    private Long accountId;

    @NonNull
    private TransactionCategory category;

    @NonNull
    private BigDecimal amount;

    @NonNull
    private TransactionType type;

    @NonNull
    @JsonProperty("tx_date")
    private OffsetDateTime txDate;

    private String note;

    @NonNull
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

}

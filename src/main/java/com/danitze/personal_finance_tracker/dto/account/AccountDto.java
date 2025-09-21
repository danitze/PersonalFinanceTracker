package com.danitze.personal_finance_tracker.dto.account;

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
public class AccountDto {

    @NonNull
    private Long id;

    @NonNull
    @JsonProperty("user_id")
    private Long userId;

    @NonNull
    private String name;

    @NonNull
    private String currency;

    @NonNull
    private BigDecimal balance;

    @NonNull
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

}

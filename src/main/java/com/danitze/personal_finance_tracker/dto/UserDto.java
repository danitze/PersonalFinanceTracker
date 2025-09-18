package com.danitze.personal_finance_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NonNull
    private Long id;

    @NonNull
    private String email;

    @NonNull
    private OffsetDateTime createdAt;

}

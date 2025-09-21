package com.danitze.personal_finance_tracker.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    @NonNull
    private String token;

    @NonNull
    @JsonProperty(value = "refresh_token")
    private String refreshToken;

}

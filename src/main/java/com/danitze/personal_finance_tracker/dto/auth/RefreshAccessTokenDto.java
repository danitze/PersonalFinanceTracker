package com.danitze.personal_finance_tracker.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshAccessTokenDto {

    @NotBlank
    @JsonProperty(value = "refresh_token")
    private String refreshToken;

}

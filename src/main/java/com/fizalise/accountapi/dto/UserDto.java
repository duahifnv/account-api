package com.fizalise.accountapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserDto(
    @NotBlank
    String name,
    @NotNull
    LocalDate dateOfBirth,
    @NotBlank
    String password,
    @NotBlank
    String email,
    @NotBlank
    String phone,
    @NotNull
    BigDecimal accountDeposit) {
}

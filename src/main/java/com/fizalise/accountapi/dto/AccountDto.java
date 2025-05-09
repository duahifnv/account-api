package com.fizalise.accountapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AccountDto(@NotNull @Positive BigDecimal balance) {
}

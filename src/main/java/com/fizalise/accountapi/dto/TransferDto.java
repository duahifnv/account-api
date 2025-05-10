package com.fizalise.accountapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record TransferDto(
        @Schema(description = "ID пользователя для перевода", example = "2")
        Long transferToUserId,
        @Schema(description = "Сумма перевода", example = "10.0")
        BigDecimal transferAmount) {
}

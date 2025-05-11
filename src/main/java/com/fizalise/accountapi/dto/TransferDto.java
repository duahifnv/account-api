package com.fizalise.accountapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TransferDto(
        @NotNull
        @Schema(description = "ID пользователя для перевода", example = "2")
        Long transferToUserId,
        @NotNull
        @Schema(description = "Сумма перевода", example = "10.0")
        @DecimalMin("0.0")
        double transferAmount) {
}

package com.fizalise.accountapi.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDto(
        @JsonView(Views.Private.class)
        @Schema(description = "ID счета", example = "1")
                         Long id,
        @JsonView(Views.Private.class)
                         @Schema(description = "Текущий баланс", example = "100.0")
                         BigDecimal balance,
        @JsonView(Views.Private.class)
                         @Schema(description = "Максимальный баланс, до которого автоматически повысят счет",
                                 example = "207.0")
                         BigDecimal maxBalance,
        @JsonView(Views.Private.class)
                         @Schema(description = "Дата последнего обновления баланса", exampleClasses = LocalDateTime.class)
                         LocalDateTime lastBalanceUpdate) {
}

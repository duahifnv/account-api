package com.fizalise.accountapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDto(Long id,
                         BigDecimal balance,
                         BigDecimal maxBalance,
                         LocalDateTime lastBalanceUpdate) {
}

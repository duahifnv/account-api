package com.fizalise.accountapi.dto;

import java.math.BigDecimal;

public record TransferDto(Long transferToUserId, BigDecimal transferAmount) {
}

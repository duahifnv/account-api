package com.fizalise.accountapi.dto.validation;

import io.swagger.v3.oas.annotations.media.Schema;

public record MethodNotSupportedResponse(@Schema(description = "Сообщение о неподдерживаемых методах")
                                         String message,
                                         @Schema(description = "Список поддерживаемых методов")
                                         String[] supportedMethods) {
}

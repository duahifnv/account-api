package com.fizalise.accountapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Schema(description = "Стандартный формат ошибки API")
public record ErrorResponse(
    @Schema(description = "HTTP статус код", example = "500")
    int status,
    
    @Schema(description = "Описание ошибки", example = "Internal Server Error")
    String error,
    
    @Schema(description = "Сообщение об ошибке", example = "Произошла непредвиденная ошибка")
    String message,
    
    @Schema(description = "Временная метка ошибки", example = "2023-07-20T12:34:56.789Z")
    Instant timestamp
) {
    public ErrorResponse(HttpStatus status, String message) {
        this(status.value(), status.getReasonPhrase(), message, Instant.now());
    }
}
package com.fizalise.accountapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record JwtDto(
        @Schema(description = "JWT-токен", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token) {
}
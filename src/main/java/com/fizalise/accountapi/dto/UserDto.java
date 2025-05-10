package com.fizalise.accountapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record UserDto(
        @NotBlank
        @Schema(description = "Имя пользователя", example = "John Smith")
        String name,
        @NotNull
        @Schema(description = "Дата рождения", example = "01.01.1990")
        LocalDate dateOfBirth,
        @NotBlank
        @Schema(description = "Пароль пользователя", example = "john0101")
        String password,
        @NotBlank
        @Email(message = "Необходим почтовый формат")
        @Schema(description = "Почта пользователя", example = "john.smith@example.com")
        String email,
        @NotBlank
        @Schema(description = "Номер телефона пользователя", example = "79991234567")
        String phone,
        @Schema(description = "Начальный депозит в счет", example = "100.0")
        @NotNull
        BigDecimal accountDeposit) {
}

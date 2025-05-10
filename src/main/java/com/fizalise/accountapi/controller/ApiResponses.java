package com.fizalise.accountapi.controller;

import com.fizalise.accountapi.dto.ErrorResponse;
import com.fizalise.accountapi.dto.validation.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
@Operation(summary = "Обновление данных пользователя")
@ApiResponse(responseCode = "200", description = "Успешно")
@ApiResponse(responseCode = "400", description = "Невалидные данные",
        content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
@ApiResponse(responseCode = "401", description = "Не авторизован")
@ApiResponse(responseCode = "403", description = "Доступ запрещен")
@ApiResponse(
        responseCode = "500",
        description = "Внутренняя ошибка сервера",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
)
public @interface ApiResponses {
}

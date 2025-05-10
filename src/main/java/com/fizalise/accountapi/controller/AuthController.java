package com.fizalise.accountapi.controller;

import com.fizalise.accountapi.dto.AuthDto;
import com.fizalise.accountapi.dto.ErrorResponse;
import com.fizalise.accountapi.dto.JwtDto;
import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для регистрации и аутентификации пользователей")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя и возвращает JWT токен",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для регистрации",
                    required = true
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "Пользователь успешно зарегистрирован",
                            content = @Content(schema = @Schema(implementation = JwtDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public JwtDto registerNewUser(@RequestBody @Valid UserDto userDto) {
        return authService.registerNewUser(userDto);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверяет учетные данные и возвращает JWT токен",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для входа",
                    required = true
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешная аутентификация",
                            content = @Content(schema = @Schema(implementation = JwtDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Неверные учетные данные"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public JwtDto authorizeUser(@RequestBody @Valid AuthDto authDto) {
        return authService.authenticateUser(authDto);
    }
}

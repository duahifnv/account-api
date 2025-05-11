package com.fizalise.accountapi.controller;

import com.fizalise.accountapi.dto.AuthDto;
import com.fizalise.accountapi.dto.JwtDto;
import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping("/login")
    @ApiResponses
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверяет учетные данные и возвращает JWT токен",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для входа",
                    required = true
            )
    )
    public JwtDto authorizeUser(@RequestBody @Valid AuthDto authDto) {
        return authService.authenticateUser(authDto);
    }
}

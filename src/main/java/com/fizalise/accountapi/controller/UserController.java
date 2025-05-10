package com.fizalise.accountapi.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fizalise.accountapi.dto.UserResponseDto;
import com.fizalise.accountapi.dto.Views;
import com.fizalise.accountapi.dto.validation.ValidationErrorResponse;
import com.fizalise.accountapi.mapper.UserMapper;
import com.fizalise.accountapi.service.user.UserService;
import com.fizalise.accountapi.validation.ValidPhoneNumber;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "User Management", description = "API для управления пользователями")
@Validated
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    @JsonView(Views.Public.class)
    @Operation(
            summary = "Получить список пользователей",
            description = "Возвращает список пользователей с пагинацией и фильтрацией",
            parameters = {
                    @Parameter(name = "key", description = "Поле для фильтрации", example = "name"),
                    @Parameter(name = "value", description = "Значение для фильтрации", example = "John"),

                    @Parameter(
                            name = "page",
                            description = "Номер страницы (начинается с 0)",
                            example = "0",
                            schema = @Schema(defaultValue = "0", minimum = "0")
                    ),
                    @Parameter(
                            name = "size",
                            description = "Количество элементов на странице",
                            example = "20",
                            schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100")
                    ),
                    @Parameter(
                            name = "sort",
                            description = "Поля сортировки в формате: property,asc|desc",
                            example = "name,asc",
                            schema = @Schema(defaultValue = "id,asc")
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос"),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на сервере"),
            }
    )
    public List<UserResponseDto> getUsers(
            @RequestParam(required = false) String key,
            @RequestParam(required = false) String value,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            @Parameter(hidden = true)
            Pageable pageable
    ) {
        return userMapper.toUserResponseDtoList(
                userService.findAllUsers(key,
                                value,
                                pageable.getPageSize(),
                                pageable.getPageNumber(),
                                pageable.getSort())
                        .stream().toList()
        );
    }

    @GetMapping("/me")
    @JsonView(Views.Private.class)
    @Operation(
            summary = "Получить текущего пользователя",
            description = "Возвращает полную информацию о текущем аутентифицированном пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "500", description = "Ошибка на сервере")
            }
    )
    public UserResponseDto getCurrentUser(Authentication authentication) {
        return userMapper.toUserResponseDto(
                userService.findByUsername(authentication.getName())
        );
    }

    @PutMapping("/me/update/phone")
    @Operation(
            summary = "Обновление номера телефона",
            description = "Изменяет номер телефона текущего аутентифицированного пользователя",
            parameters = {
                    @Parameter(name = "oldPhone", description = "Текущий номер телефона", required = true,
                            schema = @Schema(pattern = "^7\\d{10}$", example = "79991234567")),
                    @Parameter(name = "newPhone", description = "Новый номер телефона", required = true,
                            schema = @Schema(pattern = "^7\\d{10}$", example = "79001234567"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Номер успешно изменен"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "409", description = "Новый номер уже используется")
            }
    )
    public void updateCurrentUserPhone(
            @RequestParam
            @ValidPhoneNumber(message = "Текущий номер должен быть в формате 7XXXXXXXXXX")
            String oldPhone,
            @RequestParam
            @ValidPhoneNumber(message = "Новый номер должен быть в формате 7XXXXXXXXXX")
            String newPhone,
            @Parameter(hidden = true)
            Authentication authentication) {
        userService.updateUserPhone(authentication, oldPhone, newPhone);
    }

    @PutMapping("/me/update/email")
    @Operation(
            summary = "Обновление email пользователя",
            description = "Изменяет email текущего аутентифицированного пользователя",
            parameters = {
                    @Parameter(name = "oldEmail", description = "Текущий email", required = true,
                            schema = @Schema(type = "string", format = "email", example = "john.smith@example.com")),
                    @Parameter(name = "newEmail", description = "Новый email", required = true,
                            schema = @Schema(type = "string", format = "email", example = "john1.smith@example.com"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email успешно изменен"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    public void updateCurrentUserEmail(@RequestParam
                                       @Email(message = "Необходим почтовый формат")
                                       String oldEmail,
                                       @RequestParam
                                       @Email(message = "Необходим почтовый формат")
                                       String newEmail,
                                       @Parameter(hidden = true)
                                       Authentication authentication) {
        userService.updateUserEmail(authentication, oldEmail, newEmail);
    }
}

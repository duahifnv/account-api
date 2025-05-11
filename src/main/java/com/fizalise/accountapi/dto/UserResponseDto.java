package com.fizalise.accountapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserResponseDto(
        @JsonView(Views.Public.class)
        @Schema(description = "Уникальный идентификатор пользователя", example = "1")
        Long id,

        @JsonView(Views.Public.class)
        @Schema(description = "Имя пользователя", example = "John Smith")
        String name,

        @JsonView(Views.Public.class)
        @Schema(description = "Дата рождения в формате dd.MM.yyyy", example = "01.01.1990")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dateOfBirth,

        @JsonView(Views.Private.class)
        @Schema(description = "Список email-адресов пользователя",
                example = "[\"john.smith@example.com\", \"john@work.com\"]")
        List<String> emails,

        @JsonView(Views.Private.class)
        @Schema(description = "Список телефонов пользователя",
                example = "[\"79991234567\", \"74951234567\"]")
        List<String> phones,

        @JsonView(Views.Public.class)
        @Schema(description = "Идентификатор счета пользователя", example = "123")
        Long accountId,

        @JsonView(Views.Private.class)
        @Schema(description = "Подробная информация о счете пользователя")
        AccountDto accountInfo
) {
}

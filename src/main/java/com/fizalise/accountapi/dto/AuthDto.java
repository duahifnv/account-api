package com.fizalise.accountapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record AuthDto(@NotBlank @Length(min = 4, message = "Длина имени от 4 символов")
                      @Schema(description = "Имя пользователя (почта / номер телефона)",
                              examples = {"john.smith@example.com", "79991234567"})
                      String username,
                      @NotBlank @Length(min = 6, message = "Длина пароля от 6 символов")
                      @Schema(description = "Пароль пользователя", example = "john0101")
                      String password) {
}

package com.fizalise.accountapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record AuthDto(@NotBlank @Length(min = 4, message = "Длина имени от 4 символов")
                      String username,
                      @NotBlank @Length(min = 6, message = "Длина пароля от 6 символов")
                      String password) {
}

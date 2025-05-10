package com.fizalise.accountapi.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;

import java.util.List;

@Builder
public record UserResponseDto(
        @JsonView(Views.Public.class) Long id,
        @JsonView(Views.Public.class) String name,
        @JsonView(Views.Public.class) String dateOfBirth,
        @JsonView(Views.Private.class) List<String> emails,
        @JsonView(Views.Private.class) List<String> phones,
        @JsonView(Views.Public.class) Long accountId,
        @JsonView(Views.Private.class) AccountDto accountInfo
) {
}

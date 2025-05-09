package com.fizalise.accountapi.dto;

import lombok.Builder;

@Builder
public record AuthDto(String username, String password) {
}

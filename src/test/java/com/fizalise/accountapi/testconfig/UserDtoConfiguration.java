package com.fizalise.accountapi.testconfig;

import com.fizalise.accountapi.dto.UserDto;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@TestConfiguration
public class UserDtoConfiguration {
    @Bean
    public UserDto userDto() {
        return UserDto.builder()
                .name("John Doe")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .password("password")
                .email("john@mail.com")
                .phone("79991234567")
                .accountDeposit(BigDecimal.valueOf(100))
                .build();
    }
}

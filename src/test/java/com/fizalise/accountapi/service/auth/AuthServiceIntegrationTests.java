package com.fizalise.accountapi.service.auth;

import com.fizalise.accountapi.dto.AuthDto;
import com.fizalise.accountapi.dto.JwtDto;
import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.service.AuthService;
import com.fizalise.accountapi.testconfig.TestcontainersConfiguration;
import com.fizalise.accountapi.testconfig.UserDtoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Import({TestcontainersConfiguration.class, UserDtoConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthServiceIntegrationTests {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserDto userDto;
    @BeforeEach
    void registerUser() {
        JwtDto jwtDto = authService.registerNewUser(userDto);
        System.out.println("Сгенерированный токен: " + jwtDto);
    }
    @Test
    void authenticateUser_success() {
        AuthDto authDto = AuthDto.builder()
                .username(userDto.email())
                .password(userDto.password())
                .build();
        assertDoesNotThrow(() -> {
            JwtDto jwtDto = authService.authenticateUser(authDto);
            System.out.println("Полученный авторизационный токен: " + jwtDto);
        });
    }
}
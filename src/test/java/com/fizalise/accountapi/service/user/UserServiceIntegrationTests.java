package com.fizalise.accountapi.service.user;

import com.fizalise.accountapi.testconfig.TestcontainersConfiguration;
import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserServiceIntegrationTests {
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Test
    void shouldCreateUser() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .password("password")
                .email("john@mail.com")
                .phone("79991234567")
                .startBalance(BigDecimal.TEN)
                .build();
        User created = userService.createUser(userDto);
        assertEquals(userDto.name(), created.getName());
        assertEquals(userDto.dateOfBirth(), created.getDateOfBirth());
        assertTrue(passwordEncoder.matches(userDto.password(), created.getPassword()));
        assertEquals(userDto.email(), created.getEmails().stream().findFirst().get().getEmail());
        assertEquals(userDto.phone(), created.getPhones().stream().findFirst().get().getPhone());
        assertEquals(userDto.startBalance(), created.getAccount().getBalance());
    }
}

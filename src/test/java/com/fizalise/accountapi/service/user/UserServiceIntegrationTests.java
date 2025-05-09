package com.fizalise.accountapi.service.user;

import com.fizalise.accountapi.repository.UserRepository;
import com.fizalise.accountapi.testconfig.TestcontainersConfiguration;
import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserServiceIntegrationTests {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userDto = UserDto.builder()
                .name("John Doe")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .password("password")
                .email("john@mail.com")
                .phone("79991234567")
                .startBalance(BigDecimal.TEN)
                .build();
    }

    @Test
    @Transactional
    void shouldCreateUser() {
        User created = userService.createUser(userDto);
        assertEquals(userDto.name(), created.getName());
        assertEquals(userDto.dateOfBirth(), created.getDateOfBirth());
        assertTrue(passwordEncoder.matches(userDto.password(), created.getPassword()));
        assertEquals(userDto.email(), getUserFirstEmail(created));
        assertEquals(userDto.phone(), getUserFirstPhone(created));
        assertEquals(userDto.startBalance(), created.getAccount().getBalance());
    }

    @Test
    @Transactional
    void shouldUpdateUserPhone() {
        User user = userService.createUser(userDto);
        String userPhone = getUserFirstPhone(user);
        String userEmail = getUserFirstEmail(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userPhone, user.getPassword());
        userService.updateUserPhone(authentication, "79991234567", "79991234568");

        user = userService.findByUsername(userEmail);
        String updatedUserPhone = getUserFirstPhone(user);

        assertEquals("79991234568", updatedUserPhone);
    }

    @Test
    @Transactional
    void shouldUpdateUserEmail() {
        User user = userService.createUser(userDto);
        String userPhone = getUserFirstPhone(user);
        String userEmail = getUserFirstEmail(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, user.getPassword());
        userService.updateUserEmail(authentication, "john@mail.com", "john1@mail.com");

        user = userService.findByUsername(userPhone);
        String updatedUserEmail = getUserFirstEmail(user);

        assertEquals("john1@mail.com", updatedUserEmail);
    }

    @Transactional
    String getUserFirstEmail(User user) {
        return user.getEmails().stream()
                .findFirst().orElseThrow(() -> new RuntimeException("Почта не найдена"))
                .getEmail();
    }

    @Transactional
    String getUserFirstPhone(User user) {
        return user.getPhones().stream()
                .findFirst().orElseThrow(() -> new RuntimeException("Телефон не найден"))
                .getPhone();
    }
}

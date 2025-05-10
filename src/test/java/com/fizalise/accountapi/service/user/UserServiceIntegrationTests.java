package com.fizalise.accountapi.service.user;

import com.fizalise.accountapi.repository.UserRepository;
import com.fizalise.accountapi.testconfig.TestcontainersConfiguration;
import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    UserDto userDto1;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userDto = UserDto.builder()
                .name("John Doe")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .password("password")
                .email("john@mail.com")
                .phone("79991234567")
                .accountDeposit(BigDecimal.valueOf(100))
                .build();
        userDto1 = UserDto.builder()
                .name("Jane Doe")
                .dateOfBirth(LocalDate.of(1980, 2, 2))
                .password("password")
                .email("jane@mail.com")
                .phone("79992345678")
                .accountDeposit(BigDecimal.valueOf(200))
                .build();
    }

    @Test
    void shouldFindAllUsers() {
        findAllUsersByKeyAndValue(null, null);
    }

    void findAllUsersByKeyAndValue(String key, String value) {
        // when
        User created = userService.createUser(userDto);
        User created1 = userService.createUser(userDto1);
        PageImpl<User> userPage = new PageImpl<>(List.of(created, created1));

        // then
        List<User> expectedContent = userPage.getContent();
        List<User> actualContent = userService.findAllUsers(key, value, 2, 0).getContent();

        Assertions.assertThat(actualContent)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "name", "dateOfBirth")
                .ignoringCollectionOrder()
                .isEqualTo(expectedContent);
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
        assertEquals(userDto.accountDeposit(), created.getAccount().getBalance());
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

    @ParameterizedTest
    @MethodSource("provideBalanceUpdateScenarios")
    @Transactional
    public void balanceUpdateSuccess(BigDecimal initialBalance, BigDecimal maxBalance, int updateCount, BigDecimal expectedBalance) throws InterruptedException {
        User created = userService.createUser(userDto);
        created.getAccount().setBalance(initialBalance);
        created.getAccount().setMaxBalance(maxBalance);

        for (int i = 0; i < updateCount; i++) {
            Thread.sleep(100L);
            userService.updateAllAccountBalances();
        }

        String userEmail = getUserFirstEmail(created);
        User updatedUser = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        assertEquals(expectedBalance.doubleValue(), updatedUser.getAccount().getBalance().doubleValue(), 0.001);
    }

    private static Stream<Arguments> provideBalanceUpdateScenarios() {
        return Stream.of(
            Arguments.of(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(150),
                3,
                BigDecimal.valueOf(133.1)
            ),
            Arguments.of(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(120),
                5,
                BigDecimal.valueOf(120)
            ),
            Arguments.of(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(2000),
                2,
                BigDecimal.valueOf(1210)
            )
        );
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

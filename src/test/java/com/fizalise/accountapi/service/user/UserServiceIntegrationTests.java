package com.fizalise.accountapi.service.user;

import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.exception.ResourceNotFoundException;
import com.fizalise.accountapi.repository.UserRepository;
import com.fizalise.accountapi.testconfig.TestcontainersConfiguration;
import com.fizalise.accountapi.testconfig.UserDtoConfiguration;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Import({TestcontainersConfiguration.class, UserDtoConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserServiceIntegrationTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDto johnDto;
    @Autowired
    private UserDto janeDto;

    @BeforeEach
    void clearUserRepository() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldCreateUser() {
        User created = userService.createUser(johnDto);
        assertEquals(johnDto.name(), created.getName());
        assertEquals(johnDto.dateOfBirth(), created.getDateOfBirth());
        assertTrue(passwordEncoder.matches(johnDto.password(), created.getPassword()));
        assertEquals(johnDto.email(), userService.getUserFirstEmail(created));
        assertEquals(johnDto.phone(), userService.getUserFirstPhone(created));
        assertEquals(johnDto.accountDeposit(), created.getAccount().getBalance());
    }

    @Test
    @Transactional
    void shouldUpdateUserPhone() {
        User user = userService.createUser(johnDto);
        String userPhone = userService.getUserFirstPhone(user);
        String userEmail = userService.getUserFirstEmail(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userPhone, user.getPassword());
        userService.updateUserPhone(authentication, "79991234567", "79991234568");

        user = userService.findByUsername(userEmail);
        String updatedUserPhone = userService.getUserFirstPhone(user);

        assertEquals("79991234568", updatedUserPhone);
    }

    @Test
    @Transactional
    void shouldUpdateUserEmail() {
        User user = userService.createUser(johnDto);
        String userPhone = userService.getUserFirstPhone(user);
        String userEmail = userService.getUserFirstEmail(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, user.getPassword());
        userService.updateUserEmail(authentication, "john@mail.com", "john1@mail.com");

        user = userService.findByUsername(userPhone);
        String updatedUserEmail = userService.getUserFirstEmail(user);

        assertEquals("john1@mail.com", updatedUserEmail);
    }

    @Test
    @Transactional
    void shouldDeleteUserEmail() {
        User user = userService.createUser(johnDto);
        String userPhone = userService.getUserFirstPhone(user);
        String userEmail = userService.getUserFirstEmail(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, user.getPassword());
        userService.deleteUserEmail(authentication, "john@mail.com");

        user = userService.findByUsername(userPhone);
        User finalUser = user;
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserFirstEmail(finalUser));
    }

    @ParameterizedTest
    @MethodSource("provideBalanceUpdateScenarios")
    @Transactional
    public void balanceUpdateSuccess(BigDecimal initialBalance, BigDecimal maxBalance, int updateCount, BigDecimal expectedBalance) throws InterruptedException {
        User created = userService.createUser(johnDto);
        created.getAccount().setBalance(initialBalance);
        created.getAccount().setMaxBalance(maxBalance);

        for (int i = 0; i < updateCount; i++) {
            Thread.sleep(100L);
            userService.updateAllAccountBalances();
        }

        String userEmail = userService.getUserFirstEmail(created);
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
}

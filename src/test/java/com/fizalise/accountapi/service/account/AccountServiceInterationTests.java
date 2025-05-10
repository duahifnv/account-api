package com.fizalise.accountapi.service.account;

import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.entity.Account;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.repository.UserRepository;
import com.fizalise.accountapi.service.AccountService;
import com.fizalise.accountapi.service.user.UserService;
import com.fizalise.accountapi.testconfig.TestcontainersConfiguration;
import com.fizalise.accountapi.testconfig.UserDtoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({TestcontainersConfiguration.class, UserDtoConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jpa.show-sql=false",
        "account.increase.interval=999s"
})
public class AccountServiceInterationTests {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserDto johnDto;
    @Autowired
    private UserDto janeDto;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearUserRepository() {
        userRepository.deleteAll();
    }

    @Test
    void transferMoney_isThreadSafe() throws Exception {
        User john = userService.createUser(johnDto);
        User jane = userService.createUser(janeDto);
        Account johnAccount = john.getAccount();
        Account janeAccount = jane.getAccount();

        BigDecimal initialJohnBalance = johnAccount.getBalance();
        BigDecimal initialJaneBalance = janeAccount.getBalance();
        BigDecimal transferAmount = BigDecimal.valueOf(20);
        int numberOfThreads = 5;
        int transfersPerThread = 10;

        CompletableFuture<?>[] futures = new CompletableFuture[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < transfersPerThread; j++) {
                    accountService.transferMoney(johnAccount, janeAccount, transferAmount);
                    accountService.transferMoney(janeAccount, johnAccount, transferAmount);
                }
            });
        }

        CompletableFuture.allOf(futures).get(1, TimeUnit.MINUTES);

        User updatedJohn = userService.findByEmail(johnDto.email())
                .orElseThrow(() -> new RuntimeException("John не найден"));
        User updatedJane = userService.findByEmail(janeDto.email())
                .orElseThrow(() -> new RuntimeException("Jane не найдена"));

        assertEquals(0, initialJohnBalance.compareTo(updatedJohn.getAccount().getBalance()),
                "Баланс John должен остаться неизменным после равных переводов");
        assertEquals(0, initialJaneBalance.compareTo(updatedJane.getAccount().getBalance()),
                "Баланс Jane должен остаться неизменным после равных переводов");
    }
}

package com.fizalise.accountapi.service;

import com.fizalise.accountapi.entity.Account;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.repository.AccountRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Сервис управления счетами")
public class AccountService {
    private final AccountRepository accountRepository;
    @Value("${account.max-balance-coefficient}")
    private static final double MAX_BALANCE_COEFFICIENT = 2.07;
    @Transactional
    public Account createAccount(User user, BigDecimal accountDeposit) {
        Account account = Account.builder()
                .user(user)
                .balance(accountDeposit)
                .maxBalance(accountDeposit.multiply(
                            BigDecimal.valueOf(MAX_BALANCE_COEFFICIENT))
                )
                .lastBalanceUpdate(LocalDateTime.now())
                .build();
        return accountRepository.save(account);
    }
    @Transactional
    public void updateBalance(Account account, BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Размер баланса должен быть положительным значением");
        }
        account.setBalance(balance);
        account.setLastBalanceUpdate(LocalDateTime.now());
        accountRepository.save(account);
    }
    @Transactional
    public void transferMoney(@NotNull Account from, @NotNull Account to, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Размер перевода должен быть положительным значением");
        }
        if (from.getId().equals(to.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Невозможно перевести на счет отправителя");
        }
        synchronized (from.getId().compareTo(to.getId()) < 0 ? from : to) {
            synchronized (from.getId().compareTo(to.getId()) < 0 ? to : from) {
                BigDecimal fromBalance = from.getBalance();
                BigDecimal toBalance = to.getBalance();
                if (fromBalance.compareTo(amount) < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Недостаточно средств для перевода. Требуется: " + amount + ", на счету: " + fromBalance);
                }

                updateBalance(from, fromBalance.subtract(amount));
                updateBalance(to, toBalance.add(amount));
                log.debug("Перевод {} от {} к {}", amount, from.getUser().getName(), to.getUser().getName());
            }
        }
    }
}

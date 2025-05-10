package com.fizalise.accountapi.service;

import com.fizalise.accountapi.entity.Account;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
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
}

package com.fizalise.accountapi.service;

import com.fizalise.accountapi.entity.Account;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    @Transactional
    public Account createAccount(User user, BigDecimal startBalance) {
        Account account = Account.builder()
                .user(user)
                .balance(startBalance)
                .build();
        return accountRepository.save(account);
    }
}

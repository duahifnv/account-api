package com.fizalise.accountapi.controller;

import com.fizalise.accountapi.dto.TransferDto;
import com.fizalise.accountapi.entity.Account;
import com.fizalise.accountapi.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/transfer")
    public void doTransfer(Authentication authentication,
                           @Valid @RequestBody TransferDto transferDto) {
        Long userId = Long.valueOf(authentication.getName());
        Account userAccount = accountService.getAccountById(userId);
        Account tranferAccount = accountService.getAccountById(transferDto.transferToUserId());

        accountService.transferMoney(userAccount, tranferAccount, transferDto.transferAmount());
    }
}

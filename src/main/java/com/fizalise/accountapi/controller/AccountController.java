package com.fizalise.accountapi.controller;

import com.fizalise.accountapi.dto.TransferDto;
import com.fizalise.accountapi.entity.Account;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.service.AccountService;
import com.fizalise.accountapi.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Account Management", description = "API для управления банковскими счетами")
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;

    @PostMapping("/transfer")
    @ApiResponses
    @Operation(
            summary = "Перевод средств между счетами",
            description = "Выполняет перевод указанной суммы с текущего счета пользователя на другой счет",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для перевода",
                    required = true
            )
    )
    public void doTransfer(
            @Parameter(hidden = true) Authentication authentication,
            @Valid @RequestBody TransferDto transferDto) {
        User user = userService.findByUsername(authentication.getName());
        User transferUser = userService.findById(transferDto.transferToUserId());

        Account userAccount = accountService.getAccountByUser(user);
        Account tranferAccount = accountService.getAccountByUser(transferUser);

        accountService.transferMoney(userAccount, tranferAccount, transferDto.transferAmount());
    }
}

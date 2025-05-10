package com.fizalise.accountapi.controller;

import com.fizalise.accountapi.dto.AuthDto;
import com.fizalise.accountapi.dto.JwtDto;
import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public JwtDto registerNewUser(@RequestBody @Valid UserDto userDto) {
        return authService.registerNewUser(userDto);
    }

    @PostMapping("/login")
    public JwtDto authorizeUser(@RequestBody @Valid AuthDto authDto) {
        return authService.authenticateUser(authDto);
    }
}

package com.fizalise.accountapi.service;

import com.fizalise.accountapi.dto.AuthDto;
import com.fizalise.accountapi.dto.JwtDto;
import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.exception.CustomBadCredentialsException;
import com.fizalise.accountapi.exception.UserNotFoundException;
import com.fizalise.accountapi.mapper.UserMapper;
import com.fizalise.accountapi.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public record AuthService(JwtService jwtService,
                          UserService userService,
                          UserMapper userMapper,
                          AuthenticationManager authenticationManager) {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public JwtDto registerNewUser(UserDto userDto) {
        User user = userService.createUser(userDto);
        log.info("Зарегистрирован новый пользователь: {}", user);
        return new JwtDto(jwtService.generateToken(user));
    }

    public JwtDto authenticateUser(AuthDto authDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authDto.username(),
                            authDto.password()
                    )
            );
            log.info("Пользователь {} успешно аутентифицировал себя", authDto.username());
            User user = userService().findByUsername(authDto.username());
            return new JwtDto(jwtService.generateToken(user));
        } catch (BadCredentialsException e) {
            throw new CustomBadCredentialsException();
        } catch (InternalAuthenticationServiceException e) {
            throw new UserNotFoundException();
        }
    }
}

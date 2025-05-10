package com.fizalise.accountapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistsException extends ResponseStatusException {
    public UserAlreadyExistsException(String username) {
        super(HttpStatus.BAD_REQUEST, "Пользователь %s уже существует".formatted(username));
    }
}

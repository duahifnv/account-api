package com.fizalise.accountapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ForbiddenException extends ResponseStatusException {
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, "Доступ к операции запрещен");
    }
    public ForbiddenException(String reason) {
        super(HttpStatus.FORBIDDEN, reason);
    }
}

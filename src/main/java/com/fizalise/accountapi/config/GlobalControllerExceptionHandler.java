package com.fizalise.accountapi.config;

import com.fizalise.accountapi.dto.validation.MethodNotSupportedResponse;
import com.fizalise.accountapi.dto.validation.ValidationErrorResponse;
import com.fizalise.accountapi.dto.validation.Violation;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
@Slf4j(topic = "Глобальный обработчик")
// todo: Обработка PropertyReferenceException
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception e) {
        if (e instanceof ResponseStatusException) {
            throw (ResponseStatusException) e;
        }

        log.error("Unhandled exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
        return "Сбой на сервере";
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handle(DataIntegrityViolationException e) {
        log.warn("Нарушение целостности данных: {}", e.getMessage());
        return ResponseEntity.badRequest().body("Действие нарушает целостность данных");
    }
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handle(NoResourceFoundException e) {
        log.warn("Не найден ресурс: " + e.getResourcePath());
        return ResponseEntity.status(e.getStatusCode()).body(e.getBody());
    }
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handle(JwtException e) {
        log.warn("Jwt exception: " + e.getMessage());
        return "Невалидный токен";
    }
    // Обработка ошибок валидации параметров запроса и переменных пути запроса
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handle(ConstraintViolationException e) {
        List<Violation> violations = e.getConstraintViolations().stream()
                .map(v -> new Violation(
                        v.getPropertyPath().toString(), v.getMessage()
                )).toList();
        return new ValidationErrorResponse(violations);
    }
    // Обработка ошибок полей тела запроса
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handle(MethodArgumentNotValidException e) {
        List<Violation> fieldViolations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(
                        error.getField(), error.getDefaultMessage()
                ))
                .toList();
        return new ValidationErrorResponse(fieldViolations);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException: " + e.getMessage());
        return "Неверный тип аргумента: " + e.getParameter().getParameter().getName();
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(MissingServletRequestParameterException e) {
        log.warn("MissingServletRequestParameterException: " + e.getMessage());
        return "Отсутвует необходимый параметр: " + e.getParameterName();
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: " + e.getHttpInputMessage());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MethodNotSupportedResponse handle(HttpRequestMethodNotSupportedException e) {
        log.warn("HttpRequestMethodNotSupportedException: " + e.getMessage());
        return new MethodNotSupportedResponse(e.getMessage(), e.getSupportedMethods());
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handle(AccessDeniedException e) {
        log.warn("AccessDeniedException: " + e.getMessage());
    }
}

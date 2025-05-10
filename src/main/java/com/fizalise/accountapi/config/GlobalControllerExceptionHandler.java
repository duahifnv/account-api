package com.fizalise.accountapi.config;

import com.fizalise.accountapi.dto.ErrorResponse;
import com.fizalise.accountapi.dto.validation.MethodNotSupportedResponse;
import com.fizalise.accountapi.dto.validation.ValidationErrorResponse;
import com.fizalise.accountapi.dto.validation.Violation;
import com.fizalise.accountapi.service.user.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleAllExceptions(Exception e) {
        if (e instanceof ResponseStatusException) {
            log.debug("Исключение с кодом {}: {}", ((ResponseStatusException) e).getStatusCode(),
                    e.getMessage());
            throw (ResponseStatusException) e;
        }

        log.error("Необработанное исключение [{}]: {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера: " + e.getMessage()
        );
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handle(DataIntegrityViolationException e) {
        log.debug("Нарушение целостности данных: {}", e.getMessage());
        return ResponseEntity.badRequest().body("Действие нарушает целостность данных");
    }
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handle(NoResourceFoundException e) {
        log.debug("Не найден ресурс: {}", e.getResourcePath());
        return ResponseEntity.status(e.getStatusCode()).body(e.getBody());
    }
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handle(JwtException e) {
        log.debug("Jwt исключение: {}", e.getMessage());
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
        log.debug("Ошибка валидации параметров запроса: {}", Arrays.toString(violations.toArray()));
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
        log.debug("Ошибка в полях тела запроса: {}", Arrays.toString(fieldViolations.toArray()));
        return new ValidationErrorResponse(fieldViolations);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(MethodArgumentTypeMismatchException e) {
        log.debug("Невалидный тип аргумента: {}", e.getMessage());
        return "Неверный тип аргумента: " + e.getParameter().getParameter().getName();
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(MissingServletRequestParameterException e) {
        log.debug("Отсутствует необходимый параметр: {}", e.getMessage());
        return "Отсутствует необходимый параметр: " + e.getParameterName();
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        log.debug("Нечитаемое Http сообщение: {}", e.getHttpInputMessage());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MethodNotSupportedResponse handle(HttpRequestMethodNotSupportedException e) {
        log.debug("HTTP метод не поддерживается: {}", e.getMessage());
        return new MethodNotSupportedResponse(e.getMessage(), e.getSupportedMethods());
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handle(AccessDeniedException e) {
        log.debug("Доступ запрещен: {}", e.getMessage());
    }
}

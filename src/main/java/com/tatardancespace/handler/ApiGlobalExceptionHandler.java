package com.tatardancespace.handler;

import com.tatardancespace.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice(basePackages = "com.tatardancespace.controller.api")
@Order(1)
public class ApiGlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiGlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.warn("API 404: {}", path);

        ErrorResponse errorResponse = new ErrorResponse(
                "API ресурс не найден",
                "Not Found",
                HttpStatus.NOT_FOUND.value(),
                path
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        log.error("API ошибка: {} - {}", path, ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "Внутренняя ошибка сервера",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                path
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
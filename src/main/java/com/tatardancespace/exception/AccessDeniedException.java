package com.tatardancespace.exception;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String action) {
        super("Нет прав для " + action);
    }
}
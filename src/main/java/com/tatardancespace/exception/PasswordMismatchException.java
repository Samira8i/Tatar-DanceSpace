package com.tatardancespace.exception;

public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException() {
        super("Пароли не совпадают");
    }
}
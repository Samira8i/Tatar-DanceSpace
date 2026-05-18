package com.tatardancespace.exception;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String email) {
        super("Пользователь не найден: " + email);
    }
}
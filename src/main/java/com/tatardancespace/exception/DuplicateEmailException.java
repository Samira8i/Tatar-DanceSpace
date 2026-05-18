package com.tatardancespace.exception;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String email) {
        super("Пользователь с email " + email + " уже существует");
    }
}
package com.tatardancespace.exception;

public class DuplicateUsernameException extends BusinessException {
    public DuplicateUsernameException(String username) {
        super("Пользователь с именем " + username + " уже существует");
    }
}
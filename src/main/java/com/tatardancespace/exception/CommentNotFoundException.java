package com.tatardancespace.exception;

public class CommentNotFoundException extends BusinessException {
    public CommentNotFoundException(Long id) {
        super("Комметнарий с ID " + id + " не найден");
    }
}
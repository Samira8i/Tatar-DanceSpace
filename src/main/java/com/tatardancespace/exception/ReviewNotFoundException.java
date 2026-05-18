package com.tatardancespace.exception;

public class ReviewNotFoundException extends BusinessException {
    public ReviewNotFoundException(Long id) {
        super("Отзыв с ID " + id + " не найден");
    }
}
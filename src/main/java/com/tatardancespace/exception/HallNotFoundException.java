package com.tatardancespace.exception;

public class HallNotFoundException extends BusinessException {
    public HallNotFoundException(Long id) {
        super("Зал с ID " + id + " не найден");
    }
}
package com.tatardancespace.exception;

public class EventNotFoundException extends BusinessException {
    public EventNotFoundException(Long id) {
        super("Событие с ID " + id + " не найдено");
    }
}
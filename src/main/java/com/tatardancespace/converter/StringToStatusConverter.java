package com.tatardancespace.converter;

import com.tatardancespace.entity.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToStatusConverter implements Converter<String, Status> {
    @Override
    public Status convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            return Status.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Status.PENDING;
        }
    }
}

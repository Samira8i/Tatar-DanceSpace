package com.tatardancespace.config;

import com.tatardancespace.converter.StringToStatusConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToStatusConverter statusConverter;

    public WebConfig(StringToStatusConverter statusConverter) {
        this.statusConverter = statusConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(statusConverter);
    }
}
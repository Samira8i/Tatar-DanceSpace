package com.tatardancespace.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);

        RestTemplate restTemplate = new RestTemplate(factory);

        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Регистрируем модуль для работы с Java 8+ датами
        mapper.registerModule(new JavaTimeModule());

        // Отключаем запись дат как таймстемпов (используем ISO-формат)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Игнорируем неизвестные поля в JSON (чтобы не падать при изменениях API)
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Красивый вывод JSON (для отладки)
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        return mapper;
    }
}
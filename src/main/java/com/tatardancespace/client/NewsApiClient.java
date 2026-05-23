package com.tatardancespace.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NewsApiClient {

    private static final Logger log = LoggerFactory.getLogger(NewsApiClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.url:https://newsapi.org/v2/everything}")
    private String baseUrl;

    public NewsApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public JsonNode fetchNews(int page, int pageSize) {
        int currentPage = Math.max(0, page);
        int size = Math.min(20, Math.max(1, pageSize));

        String url = String.format(
                "%s?q=dance OR танцы&language=ru&pageSize=%d&page=%d&sortBy=publishedAt&apiKey=%s",
                baseUrl, size, currentPage + 1, apiKey
        );

        log.info("Page: {}, PageSize: {}", currentPage, size);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            log.info("Отправляем GET запрос...");
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );

            log.info("Статус ответа: {}", response.getStatusCode());

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("NewsAPI вернул НЕ 200 статус: {}", response.getStatusCode());
                return null;
            }

            String body = response.getBody();
            log.info("Длина ответа: {} символов", body != null ? body.length() : 0);

            if (body == null || body.isEmpty()) {
                log.error("Пустой ответ от NewsAPI");
                return null;
            }

            if (body.length() > 500) {
                log.info("Превью ответа (первые 500 символов):\n{}", body.substring(0, 500));
            } else {
                log.info("Полный ответ:\n{}", body);
            }

            JsonNode root = objectMapper.readTree(body);

            if (root.has("status")) {
                log.info("Статус из JSON: {}", root.get("status").asText());
            }

            if (root.has("status") && "error".equals(root.get("status").asText())) {
                String errorMsg = root.has("message") ? root.get("message").asText() : "Unknown error";
                log.error("NewsAPI вернул ошибку: {}", errorMsg);
                return null;
            }

            if (root.has("articles")) {
                log.info("Количество статей: {}", root.get("articles").size());
            }
            return root;

        } catch (Exception e) {
            log.error("Тип ошибки: {}", e.getClass().getName());
            log.error("Сообщение: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("Причина: {}", e.getCause().getMessage());
            }
            log.error("Стек ошибки:", e);
            return null;
        }
    }
}
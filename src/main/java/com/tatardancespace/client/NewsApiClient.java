package com.tatardancespace.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Получает новости с пагинацией
     * @param page номер страницы (начиная с 0)
     * @param pageSize количество новостей на странице (макс 20)
     * @return JsonNode с ответом от API или null при ошибке
     */
    public JsonNode fetchNews(int page, int pageSize) {
        int currentPage = Math.max(0, page);
        int size = Math.min(20, Math.max(1, pageSize));

        String url = String.format(
                "%s?q=dance OR танцы&language=ru&pageSize=%d&page=%d&sortBy=publishedAt&apiKey=%s",
                baseUrl, size, currentPage + 1, apiKey
        );

        try {
            log.info("Запрос к NewsAPI: {}", url.replace(apiKey, "***"));
            String response = restTemplate.getForObject(url, String.class);
            return objectMapper.readTree(response);
        } catch (Exception e) {
            log.error("Ошибка при запросе к NewsAPI: {}", e.getMessage());
            return null;
        }
    }
}
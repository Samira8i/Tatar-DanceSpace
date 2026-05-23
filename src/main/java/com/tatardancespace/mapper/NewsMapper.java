package com.tatardancespace.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.tatardancespace.dto.response.NewsResponse;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsMapper {

    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public List<NewsResponse> mapToNewsResponses(JsonNode root) {
        List<NewsResponse> newsList = new ArrayList<>();

        if (root == null || !root.has("articles")) {
            return newsList;
        }

        for (JsonNode article : root.get("articles")) {
            String title = article.has("title") ? article.get("title").asText() : "";
            if (title == null || "[Removed]".equals(title) || title.isEmpty()) {
                continue;
            }

            newsList.add(new NewsResponse(
                    title,
                    truncateDescription(article.has("description") ? article.get("description").asText() : ""),
                    article.has("url") ? article.get("url").asText() : "#",
                    extractImageUrl(article),
                    formatDate(article.has("publishedAt") ? article.get("publishedAt").asText() : "")
            ));
        }
        return newsList;
    }

    private String extractImageUrl(JsonNode article) {
        if (article.has("urlToImage") && !article.get("urlToImage").isNull()) {
            String url = article.get("urlToImage").asText();
            if (url.startsWith("http")) return url;
        }
        return "";
    }

    private String formatDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) return "";
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(rawDate);
            return zdt.format(OUTPUT_FORMATTER);
        } catch (Exception e) {
            return "";
        }
    }

    private String truncateDescription(String description) {
        if (description == null || description.isEmpty()) return "";
        return description.length() > 150 ? description.substring(0, 150) + "..." : description;
    }
}
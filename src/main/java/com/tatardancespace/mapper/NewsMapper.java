package com.tatardancespace.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.tatardancespace.dto.response.NewsResponse;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsMapper {

    public List<NewsResponse> mapToNewsResponses(JsonNode root) {
        List<NewsResponse> newsList = new ArrayList<>();

        if (root == null || !root.has("articles")) {
            return newsList;
        }

        JsonNode articles = root.get("articles");
        for (JsonNode article : articles) {
            String title = article.has("title") ? article.get("title").asText() : "";
            if (title == null || "[Removed]".equals(title) || title.isEmpty()) {
                continue;
            }

            newsList.add(new NewsResponse(
                    title,
                    truncateDescription(article.has("description") ? article.get("description").asText() : ""),
                    article.has("url") ? article.get("url").asText() : "#",
                    article.has("urlToImage") && !article.get("urlToImage").isNull() ? article.get("urlToImage").asText() : "",
                    formatDate(article.has("publishedAt") ? article.get("publishedAt").asText() : "")
            ));
        }
        return newsList;
    }

    private String truncateDescription(String description) {
        if (description == null || description.isEmpty()) return "";
        return description.length() > 150 ? description.substring(0, 150) + "..." : description;
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            String datePart = isoDate.split("T")[0];
            String[] parts = datePart.split("-");
            return parts.length == 3 ? parts[2] + "." + parts[1] + "." + parts[0] : isoDate;
        } catch (Exception e) {
            return isoDate;
        }
    }
}
package com.tatardancespace.controller.api;

import com.tatardancespace.dto.response.NewsResponse;
import com.tatardancespace.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@Tag(name = "News API", description = "Новости о танцах со всего мира (стороннее API)")
public class NewsApiController {

    private final NewsService newsService;

    public NewsApiController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    @Operation(summary = "Получить свежие новости о танцах (с пагинацией)")
    public ResponseEntity<Map<String, Object>> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<NewsResponse> news = newsService.getDanceNews(page, size);
        boolean hasMore = newsService.hasMoreNews(page + 1, size);

        Map<String, Object> response = new HashMap<>();
        response.put("news", news);
        response.put("hasMore", hasMore);
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }
}
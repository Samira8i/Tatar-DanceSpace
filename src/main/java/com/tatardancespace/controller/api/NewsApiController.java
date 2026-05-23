package com.tatardancespace.controller.api;

import com.tatardancespace.dto.response.NewsResponse;
import com.tatardancespace.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsApiController {

    private final NewsService newsService;

    public NewsApiController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        int safeSize = Math.min(20, Math.max(1, size));
        int safePage = Math.max(0, page);

        List<NewsResponse> news = newsService.getDanceNews(safePage, safeSize);

        boolean hasMore = news.size() == safeSize;


        Map<String, Object> response = new HashMap<>();
        response.put("news", news);
        response.put("hasMore", hasMore);
        response.put("currentPage", safePage);

        return ResponseEntity.ok(response);
    }
}
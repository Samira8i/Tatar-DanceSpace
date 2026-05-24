package com.tatardancespace.controller.api;

import com.tatardancespace.dto.response.NewsPageResponse;
import com.tatardancespace.dto.response.NewsResponse;
import com.tatardancespace.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsApiController {

    private final NewsService newsService;

    public NewsApiController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<NewsPageResponse> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        int safeSize = Math.min(20, Math.max(1, size));
        int safePage = Math.max(0, page);

        List<NewsResponse> news = newsService.getDanceNews(safePage, safeSize);
        boolean hasMore = news.size() == safeSize;

        return ResponseEntity.ok(new NewsPageResponse(news, hasMore, safePage));
    }
}
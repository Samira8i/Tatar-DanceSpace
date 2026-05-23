package com.tatardancespace.controller.api;

import com.tatardancespace.service.CustomUserDetails;
import com.tatardancespace.service.EventService;
import com.tatardancespace.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class FavoriteApiController {

    private final FavoriteService favoriteService;
    private final EventService eventService;

    public FavoriteApiController(FavoriteService favoriteService, EventService eventService) {
        this.favoriteService = favoriteService;
        this.eventService = eventService;
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean isFavorited = favoriteService.toggleFavorite(userDetails.getId(), id);
        long favoritesCount = favoriteService.getFavoritesCount(id);
        long commentsCount = eventService.getEventById(id).getComments().size();
        long likesCount = eventService.getEventById(id).getLikes().size();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isFavorited", isFavorited);
        response.put("favoritesCount", favoritesCount);
        response.put("commentsCount", commentsCount);
        response.put("likesCount", likesCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/favorite/status")
    public ResponseEntity<Map<String, Object>> getFavoriteStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean isFavorited = favoriteService.isFavorite(userDetails.getId(), id);
        long favoritesCount = favoriteService.getFavoritesCount(id);

        Map<String, Object> response = new HashMap<>();
        response.put("isFavorited", isFavorited);
        response.put("favoritesCount", favoritesCount);
        response.put("eventId", id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/favorites")
    @Operation(summary = "Получить все избранные события пользователя")
    public ResponseEntity<?> getUserFavorites(@AuthenticationPrincipal CustomUserDetails userDetails) {
        var favorites = favoriteService.getUserFavoriteEvents(userDetails.getId());
        return ResponseEntity.ok(favorites);
    }
}
package com.tatardancespace.controller.api;

import com.tatardancespace.dto.response.FavoriteResponse;
import com.tatardancespace.entity.Event;
import com.tatardancespace.service.CustomUserDetails;
import com.tatardancespace.service.EventService;
import com.tatardancespace.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<FavoriteResponse> toggleFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean isFavorited = favoriteService.toggleFavorite(userDetails.getId(), id);
        long favoritesCount = favoriteService.getFavoritesCount(id);
        long commentsCount = eventService.getEventById(id).getComments().size();
        long likesCount = eventService.getEventById(id).getLikes().size();

        return ResponseEntity.ok(new FavoriteResponse(true, isFavorited, favoritesCount, commentsCount, likesCount));
    }

    @GetMapping("/{id}/favorite/status")
    public ResponseEntity<FavoriteResponse> getFavoriteStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean isFavorited = favoriteService.isFavorite(userDetails.getId(), id);
        long favoritesCount = favoriteService.getFavoritesCount(id);

        return ResponseEntity.ok(new FavoriteResponse(true, isFavorited, favoritesCount, 0, 0));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Event>> getUserFavorites(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        var favorites = favoriteService.getUserFavoriteEvents(userDetails.getId());
        return ResponseEntity.ok(favorites);
    }
}
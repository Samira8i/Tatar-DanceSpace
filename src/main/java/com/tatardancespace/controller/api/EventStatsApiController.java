package com.tatardancespace.controller.api;

import com.tatardancespace.dto.response.EventStatsResponse;
import com.tatardancespace.service.CommentService;
import com.tatardancespace.service.FavoriteService;
import com.tatardancespace.service.LikeService;
import com.tatardancespace.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventStatsApiController {

    private final LikeService likeService;
    private final CommentService commentService;
    private final FavoriteService favoriteService;
    private final UserService userService;

    public EventStatsApiController(LikeService likeService, CommentService commentService,
                                   FavoriteService favoriteService, UserService userService) {
        this.likeService = likeService;
        this.commentService = commentService;
        this.favoriteService = favoriteService;
        this.userService = userService;
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<EventStatsResponse> getEventStats(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        long likesCount = likeService.getLikesCount(id);
        long commentsCount = commentService.getCommentsCount(id);
        long favoritesCount = favoriteService.getFavoritesCount(id);

        boolean isLiked = false;
        boolean isFavorited = false;
        if (userDetails != null) {
            var user = userService.findByEmail(userDetails.getUsername());
            isLiked = likeService.isLikedByUser(id, user.getId());
            isFavorited = favoriteService.isFavorite(user.getId(), id);
        }

        EventStatsResponse response = new EventStatsResponse(likesCount, commentsCount, favoritesCount, isLiked, isFavorited);
        return ResponseEntity.ok(response);
    }
}
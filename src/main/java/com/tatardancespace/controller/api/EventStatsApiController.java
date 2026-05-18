package com.tatardancespace.controller.api;

import com.tatardancespace.dto.response.EventStatsResponse;
import com.tatardancespace.entity.User;
import com.tatardancespace.service.CommentService;
import com.tatardancespace.service.LikeService;
import com.tatardancespace.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Event Stats API", description = "Статистика событий")
public class EventStatsApiController {

    private final LikeService likeService;
    private final CommentService commentService;
    private final UserService userService;

    public EventStatsApiController(LikeService likeService, CommentService commentService, UserService userService) {
        this.likeService = likeService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Получить статистику события")
    public ResponseEntity<EventStatsResponse> getStats(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        long likesCount = likeService.getLikesCount(id);
        long commentsCount = commentService.getCommentsCount(id);
        boolean isLiked = false;

        if (userDetails != null) {
            User user = userService.findByEmail(userDetails.getUsername());
            isLiked = likeService.isLikedByUser(id, user.getId());
        }

        return ResponseEntity.ok(new EventStatsResponse(likesCount, commentsCount, isLiked));
    }
}
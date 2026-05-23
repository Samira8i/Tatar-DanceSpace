package com.tatardancespace.controller.api;

import com.tatardancespace.dto.response.LikeResponse;
import com.tatardancespace.entity.User;
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
public class LikeApiController {

    private final LikeService likeService;
    private final UserService userService;

    public LikeApiController(LikeService likeService, UserService userService) {
        this.likeService = likeService;
        this.userService = userService;
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByEmail(userDetails.getUsername());
        boolean liked = likeService.toggleLike(id, user);
        long likesCount = likeService.getLikesCount(id);

        return ResponseEntity.ok(new LikeResponse(liked, likesCount));
    }
}
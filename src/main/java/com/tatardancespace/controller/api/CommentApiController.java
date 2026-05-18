package com.tatardancespace.controller.api;

import com.tatardancespace.dto.response.CommentResponse;
import com.tatardancespace.entity.Comment;
import com.tatardancespace.entity.User;
import com.tatardancespace.service.CommentService;
import com.tatardancespace.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Comments API", description = "Управление комментариями на событиях")
public class CommentApiController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentApiController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "Добавить комментарий")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @RequestParam String text,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByEmail(userDetails.getUsername());
        Comment comment = commentService.addComment(id, text, user);

        CommentResponse response = new CommentResponse(
                true,
                comment.getId(),
                user.getUsername(),
                text,
                comment.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Получить все комментарии события")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsByEventId(id));
    }
}
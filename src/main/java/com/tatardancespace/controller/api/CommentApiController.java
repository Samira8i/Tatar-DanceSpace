package com.tatardancespace.controller.api;

import com.tatardancespace.dto.response.CommentResponse;
import com.tatardancespace.entity.Comment;
import com.tatardancespace.entity.User;
import com.tatardancespace.exception.AccessDeniedException;
import com.tatardancespace.service.CommentService;
import com.tatardancespace.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class CommentApiController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentApiController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/{id}/comment")
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
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsByEventId(id));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        Comment comment = commentService.getCommentById(commentId);

        if (!comment.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new AccessDeniedException("удаления этого комментария");
        }

        commentService.deleteComment(commentId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Комментарий удалён");
        return ResponseEntity.ok(response);
    }
}
package com.tatardancespace.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentResponse {
    private boolean success;
    private Long commentId;
    private String author;
    private String text;
    private String createdAt;

    public CommentResponse() {}

    public CommentResponse(boolean success, Long commentId, String author, String text, LocalDateTime createdAt) {
        this.success = success;
        this.commentId = commentId;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : null;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
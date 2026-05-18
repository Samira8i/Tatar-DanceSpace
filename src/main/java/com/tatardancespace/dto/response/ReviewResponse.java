package com.tatardancespace.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewResponse {
    private Long id;
    private String author;
    private Integer rating;
    private String text;
    private String createdAt;
    private boolean success;

    public ReviewResponse() {}

    public ReviewResponse(boolean success) {
        this.success = success;
    }

    public ReviewResponse(Long id, String author, Integer rating, String text, LocalDateTime createdAt) {
        this.id = id;
        this.author = author;
        this.rating = rating;
        this.text = text;
        this.createdAt = createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : null;
        this.success = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
package com.tatardancespace.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Новость о танцах")
public class NewsResponse {

    @Schema(description = "Заголовок новости")
    private String title;

    @Schema(description = "Краткое описание")
    private String description;

    @Schema(description = "Ссылка на полную статью")
    private String url;

    @Schema(description = "Ссылка на изображение")
    private String imageUrl;

    @Schema(description = "Дата публикации")
    private String publishedAt;

    public NewsResponse() {}

    public NewsResponse(String title, String description, String url, String imageUrl, String publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publishedAt = publishedAt;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
}
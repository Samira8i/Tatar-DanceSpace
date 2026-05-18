package com.tatardancespace.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
//todo: спросить у анатолика как хранить картинки
public class EventRequest {

    @NotBlank(message = "Название обязательно")
    @Size(min = 3, max = 100, message = "Название от 3 до 100 символов")
    private String title;

    @NotNull(message = "Дата и время обязательны")
    @Future(message = "Дата должна быть в будущем")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateTime;

    @Size(max = 50, message = "Стиль не более 50 символов")
    private String style;

    @Size(max = 255, message = "Место не более 255 символов")
    private String place;

    @Size(max = 1000, message = "Описание не более 1000 символов")
    private String description;

    private String imageUrl;

    public EventRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
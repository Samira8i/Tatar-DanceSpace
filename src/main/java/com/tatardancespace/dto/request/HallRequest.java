package com.tatardancespace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class HallRequest {
    @NotBlank(message="Название обязательно")
    @Size(min = 2, max = 100, message = "Название должно быть от 2 до 100 символов")
    private String name;

    @NotBlank(message = "Адрес обязателен")
    private String address;

    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть положительной")
    private Double price;

    @Size(max = 1000, message = "Описание не более 1000 символов")
    private String description;

    private String imageUrl;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

package com.tatardancespace.dto.response;

public class HallWithRatingResponse {
    private Long id;
    private String name;
    private String address;
    private Double price;
    private String description;
    private String imageUrl;
    private Double averageRating;
    private int reviewsCount;

    public HallWithRatingResponse(Long id, String name, String address, Double price,
                                  String description, String imageUrl,
                                  Double averageRating, int reviewsCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.averageRating = averageRating;
        this.reviewsCount = reviewsCount;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public Double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public Double getAverageRating() { return averageRating; }
    public int getReviewsCount() { return reviewsCount; }
}
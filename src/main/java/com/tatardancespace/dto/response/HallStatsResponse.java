package com.tatardancespace.dto.response;

public class HallStatsResponse {
    private double averageRating;
    private int reviewsCount;

    public HallStatsResponse() {}

    public HallStatsResponse(double averageRating, int reviewsCount) {
        this.averageRating = averageRating;
        this.reviewsCount = reviewsCount;
    }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(int reviewsCount) { this.reviewsCount = reviewsCount; }
}
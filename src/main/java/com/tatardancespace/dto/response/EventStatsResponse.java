package com.tatardancespace.dto.response;

public class EventStatsResponse {
    private long likesCount;
    private long commentsCount;
    private long favoritesCount;
    private boolean isLiked;
    private boolean isFavorited;

    public EventStatsResponse() {}

    public EventStatsResponse(long likesCount, long commentsCount, long favoritesCount, boolean isLiked, boolean isFavorited) {
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.favoritesCount = favoritesCount;
        this.isLiked = isLiked;
        this.isFavorited = isFavorited;
    }

    public long getLikesCount() { return likesCount; }
    public void setLikesCount(long likesCount) { this.likesCount = likesCount; }
    public long getCommentsCount() { return commentsCount; }
    public void setCommentsCount(long commentsCount) { this.commentsCount = commentsCount; }
    public long getFavoritesCount() { return favoritesCount; }
    public void setFavoritesCount(long favoritesCount) { this.favoritesCount = favoritesCount; }
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
    public boolean isFavorited() { return isFavorited; }
    public void setFavorited(boolean favorited) { isFavorited = favorited; }
}
package com.tatardancespace.dto.response;

public class FavoriteResponse {

    private boolean success;

    private boolean isFavorited;

    private long favoritesCount;

    private long commentsCount;

    private long likesCount;

    public FavoriteResponse() {}

    public FavoriteResponse(boolean success, boolean isFavorited, long favoritesCount,
                            long commentsCount, long likesCount) {
        this.success = success;
        this.isFavorited = isFavorited;
        this.favoritesCount = favoritesCount;
        this.commentsCount = commentsCount;
        this.likesCount = likesCount;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public boolean isFavorited() { return isFavorited; }
    public void setFavorited(boolean favorited) { isFavorited = favorited; }

    public long getFavoritesCount() { return favoritesCount; }
    public void setFavoritesCount(long favoritesCount) { this.favoritesCount = favoritesCount; }

    public long getCommentsCount() { return commentsCount; }
    public void setCommentsCount(long commentsCount) { this.commentsCount = commentsCount; }

    public long getLikesCount() { return likesCount; }
    public void setLikesCount(long likesCount) { this.likesCount = likesCount; }
}
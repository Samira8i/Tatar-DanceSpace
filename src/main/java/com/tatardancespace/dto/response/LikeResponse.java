package com.tatardancespace.dto.response;

public class LikeResponse {
    private boolean liked;
    private long likesCount;

    public LikeResponse() {}

    public LikeResponse(boolean liked, long likesCount) {
        this.liked = liked;
        this.likesCount = likesCount;
    }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    public long getLikesCount() { return likesCount; }
    public void setLikesCount(long likesCount) { this.likesCount = likesCount; }
}
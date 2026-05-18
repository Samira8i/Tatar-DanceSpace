package com.tatardancespace.dto.response;

public class EventStatsResponse {
    private long likesCount;
    private long commentsCount;
    private boolean isLiked;

    public EventStatsResponse() {}

    public EventStatsResponse(long likesCount, long commentsCount, boolean isLiked) {
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.isLiked = isLiked;
    }

    public long getLikesCount() { return likesCount; }
    public void setLikesCount(long likesCount) { this.likesCount = likesCount; }

    public long getCommentsCount() { return commentsCount; }
    public void setCommentsCount(long commentsCount) { this.commentsCount = commentsCount; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
}
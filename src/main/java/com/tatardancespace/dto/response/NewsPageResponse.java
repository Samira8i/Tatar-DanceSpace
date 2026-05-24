package com.tatardancespace.dto.response;

import java.util.List;

public class NewsPageResponse {

    private List<NewsResponse> news;
    private boolean hasMore;
    private int currentPage;

    public NewsPageResponse() {}

    public NewsPageResponse(List<NewsResponse> news, boolean hasMore, int currentPage) {
        this.news = news;
        this.hasMore = hasMore;
        this.currentPage = currentPage;
    }

    public List<NewsResponse> getNews() { return news; }
    public void setNews(List<NewsResponse> news) { this.news = news; }

    public boolean isHasMore() { return hasMore; }
    public void setHasMore(boolean hasMore) { this.hasMore = hasMore; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
}
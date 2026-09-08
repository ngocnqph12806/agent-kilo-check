package com.skillseed.rating.dto;

import java.util.List;

public class RatingPageResponse {

    private List<RatingResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public RatingPageResponse() {
    }

    public RatingPageResponse(List<RatingResponse> items, int page, int size,
                              long totalElements, int totalPages) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<RatingResponse> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}

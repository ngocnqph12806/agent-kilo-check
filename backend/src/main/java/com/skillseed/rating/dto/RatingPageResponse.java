package com.skillseed.rating.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @deprecated use the shared {@code com.skillseed.shared.dto.PageResponse}
 *             shape — this class adds {@code content} alongside the old
 *             {@code items} field to ease migration.
 */
@Deprecated
public class RatingPageResponse {

    @JsonProperty("content")
    @JsonAlias("items")
    private List<RatingResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    public RatingPageResponse() {
    }

    public RatingPageResponse(List<RatingResponse> items, int page, int size,
                              long totalElements, int totalPages) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = page == 0;
        this.last = page >= Math.max(1, totalPages) - 1;
        this.hasNext = !this.last;
        this.hasPrevious = !this.first;
    }

    public List<RatingResponse> getItems() {
        return items;
    }

    public List<RatingResponse> getContent() {
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

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }
}

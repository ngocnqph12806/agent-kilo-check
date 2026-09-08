package com.skillseed.discover.dto;

import org.springframework.data.domain.Sort;

/**
 * Discover sort options per FR-M32.
 * <ul>
 *   <li>{@link #RATING} (default) — ORDER BY rating_avg DESC, sessions_completed DESC</li>
 *   <li>{@link #SESSIONS_COMPLETED} — ORDER BY sessions_completed DESC, rating_avg DESC</li>
 *   <li>{@link #RECENT_ACTIVITY} — ORDER BY updated_at DESC</li>
 * </ul>
 */
public enum DiscoverSort {
    RATING("ratingAvg", Sort.Direction.DESC),
    SESSIONS_COMPLETED("sessionsCompleted", Sort.Direction.DESC),
    RECENT_ACTIVITY("updatedAt", Sort.Direction.DESC);

    private final String property;
    private final Sort.Direction direction;

    DiscoverSort(String property, Sort.Direction direction) {
        this.property = property;
        this.direction = direction;
    }

    public String property() {
        return property;
    }

    public Sort.Direction direction() {
        return direction;
    }

    public static DiscoverSort fromValue(String value) {
        if (value == null || value.isBlank()) {
            return RATING;
        }
        for (DiscoverSort s : values()) {
            if (s.name().equalsIgnoreCase(value) || s.property.equalsIgnoreCase(value)) {
                return s;
            }
        }
        return RATING;
    }
}

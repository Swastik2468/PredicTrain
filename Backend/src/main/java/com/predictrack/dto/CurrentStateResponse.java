package com.predictrack.dto;

/**
 * DTO representing the train's live position, progress along the current segment,
 * current delay (displayed to the user), and last updated timestamp.
 */
public record CurrentStateResponse(
        String currentStation,
        String nextStation,
        int progressPercentage,
        int currentDelayMinutes,
        String lastUpdated
) {
}

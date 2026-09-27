package com.predictrack.dto;

/**
 * Standardized JSON error payload returned when an API error occurs.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String timestamp
) {
}

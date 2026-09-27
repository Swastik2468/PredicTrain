package com.predictrack.dto;

/**
 * DTO representing the final destination ETA summary.
 */
public record ETAResponse(
        String destination,
        String estimatedArrival,
        int remainingMinutes
) {
}

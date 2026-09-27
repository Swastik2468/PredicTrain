package com.predictrack.dto;

/**
 * DTO containing basic train metadata for the dashboard header.
 */
public record TrainInfoResponse(
        String number,
        String name,
        String source,
        String destination
) {
}

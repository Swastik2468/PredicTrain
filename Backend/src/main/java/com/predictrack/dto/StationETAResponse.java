package com.predictrack.dto;

/**
 * DTO representing the expected arrival time at an upcoming station along the route.
 */
public record StationETAResponse(
        String station,
        String eta
) {
}

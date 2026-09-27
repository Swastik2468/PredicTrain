package com.predictrack.dto;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * DTO wrapping a single human-readable delay explanation.
 * Annotated with @JsonValue on message() so Jackson serializes the list of
 * ExplanationResponse objects as clean strings in JSON:
 *   "explanations": [
 *     "Heavy rain near Vadodara may add approximately 8 minutes.",
 *     "Congestion between Bharuch and Vadodara may add approximately 12 minutes."
 *   ]
 */
public record ExplanationResponse(
        @JsonValue String message
) {
}

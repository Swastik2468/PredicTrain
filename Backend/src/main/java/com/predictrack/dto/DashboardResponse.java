package com.predictrack.dto;

import java.util.List;

/**
 * Top-level DTO returned by GET /api/trains/{trainNumber}/dashboard.
 * Provides all information required by the frontend in a single API call.
 */
public record DashboardResponse(
        TrainInfoResponse train,
        CurrentStateResponse currentState,
        ETAResponse eta,
        DelayBreakdownResponse delayBreakdown,
        List<StationETAResponse> upcomingStations,
        List<ExplanationResponse> explanations
) {
}

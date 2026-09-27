package com.predictrack.dto;

/**
 * Feature vector payload prepared for the future Python/FastAPI (scikit-learn)
 * ML residual correction service.
 *
 * Concept:
 *   ETA_final = layeredETA + ML_residual_correction
 */
public record MLPredictionRequest(
        String trainNumber,
        String currentStationCode,
        String nextStationCode,
        int progressPercentage,
        int currentDelayMinutes,
        int baselineRunningMinutes,
        int stationDwellMinutes,
        int weatherDelayMinutes,
        int incidentDelayMinutes
) {
}

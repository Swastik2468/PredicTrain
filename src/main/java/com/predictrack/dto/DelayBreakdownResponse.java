package com.predictrack.dto;

/**
 * DTO separating the components of the remaining journey time:
 * - runningTimeMinutes: baseline/expected running time (from historical medians)
 * - stationDwellMinutes: expected station halt time (from historical medians)
 * - weatherDelayMinutes: expected delay caused by weather on remaining segments
 * - incidentDelayMinutes: expected delay caused by active operational incidents
 * - mlCorrectionMinutes: optional ML residual correction (0 when disabled)
 */
public record DelayBreakdownResponse(
        int runningTimeMinutes,
        int stationDwellMinutes,
        int weatherDelayMinutes,
        int incidentDelayMinutes,
        int mlCorrectionMinutes
) {
}

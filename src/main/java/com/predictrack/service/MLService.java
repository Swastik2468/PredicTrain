package com.predictrack.service;

import com.predictrack.entity.Train;
import com.predictrack.entity.TrainCurrentState;

/**
 * Interface for Machine Learning residual ETA correction.
 *
 * Future model concept:
 *   ETA_final = layeredETA + ML_residual_correction
 *
 * The ML model corrects residual prediction error rather than replacing
 * the explainable layered ETA calculation.
 * When disabled (predictrack.ml.enabled=false), returns 0.
 */
public interface MLService {

    int getCorrection(
            Train train,
            TrainCurrentState currentState,
            int remainingRunningMinutes,
            int remainingDwellMinutes,
            int weatherDelayMinutes,
            int incidentDelayMinutes
    );

    boolean isMlEnabled();
}

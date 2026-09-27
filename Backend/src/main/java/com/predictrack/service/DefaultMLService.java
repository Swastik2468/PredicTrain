package com.predictrack.service;

import com.predictrack.dto.MLPredictionRequest;
import com.predictrack.entity.Train;
import com.predictrack.entity.TrainCurrentState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Default implementation of MLService.
 *
 * Controlled by application.properties:
 *   predictrack.ml.enabled=false
 *
 * When disabled, returns 0 minutes of correction.
 * Structured so a future Python/FastAPI (scikit-learn) service can be called
 * using the MLPredictionRequest feature payload without altering ETAService.
 */
@Service
public class DefaultMLService implements MLService {

    private final boolean mlEnabled;

    public DefaultMLService(@Value("${predictrack.ml.enabled:false}") boolean mlEnabled) {
        this.mlEnabled = mlEnabled;
    }

    @Override
    public int getCorrection(
            Train train,
            TrainCurrentState currentState,
            int remainingRunningMinutes,
            int remainingDwellMinutes,
            int weatherDelayMinutes,
            int incidentDelayMinutes
    ) {
        if (!mlEnabled) {
            return 0;
        }

        // Prepare feature vector for future Python + FastAPI scikit-learn endpoint
        String nextStationCode = currentState.getNextStation() != null
                ? currentState.getNextStation().getStationCode()
                : currentState.getCurrentStation().getStationCode();

        MLPredictionRequest requestPayload = new MLPredictionRequest(
                train.getTrainNumber(),
                currentState.getCurrentStation().getStationCode(),
                nextStationCode,
                currentState.getProgressPercentage(),
                currentState.getCurrentDelayMinutes(),
                remainingRunningMinutes,
                remainingDwellMinutes,
                weatherDelayMinutes,
                incidentDelayMinutes
        );

        // Placeholder until FastAPI service is connected: return 0 residual minutes
        return 0;
    }

    @Override
    public boolean isMlEnabled() {
        return mlEnabled;
    }
}

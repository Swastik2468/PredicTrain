package com.predictrack.service;

import com.predictrack.entity.Train;
import com.predictrack.entity.TrainCurrentState;
import com.predictrack.provider.TrainDataProvider;
import org.springframework.stereotype.Service;

/**
 * Service responsible for retrieving and validating Train metadata and live TrainCurrentState.
 * Depends on TrainDataProvider so the data source can later be swapped without changing service logic.
 */
@Service
public class TrainService {

    private final TrainDataProvider trainDataProvider;

    public TrainService(TrainDataProvider trainDataProvider) {
        this.trainDataProvider = trainDataProvider;
    }

    /**
     * Validates the train number format and retrieves the Train entity.
     */
    public Train getTrainByNumber(String trainNumber) {
        validateTrainNumber(trainNumber);

        String trimmed = trainNumber.trim();
        return trainDataProvider.findTrainByNumber(trimmed)
                .orElseThrow(() -> new TrainNotFoundException("Train not found with number: " + trimmed));
    }

    /**
     * Retrieves and validates the train's current operational state.
     */
    public TrainCurrentState getCurrentState(Train train) {
        TrainCurrentState state = trainDataProvider.findCurrentStateByTrain(train)
                .orElseThrow(() -> new InvalidRouteDataException(
                        "Current operational state is missing for train: " + train.getTrainNumber()));

        validateCurrentState(state);
        return state;
    }

    /**
     * Validates that the train number is non-empty and consists of 3 to 6 digits (standard Indian Railways numbering).
     */
    public void validateTrainNumber(String trainNumber) {
        if (trainNumber == null || trainNumber.trim().isEmpty()) {
            throw new InvalidTrainNumberException("Invalid train number: train number cannot be empty.");
        }
        String trimmed = trainNumber.trim();
        if (!trimmed.matches("^\\d{3,6}$")) {
            throw new InvalidTrainNumberException(
                    "Invalid train number '" + trimmed + "': must contain 3 to 6 digits (e.g., 12901).");
        }
    }

    /**
     * Validates that progressPercentage is within 0-100 and delay is not an invalid negative outlier.
     */
    public void validateCurrentState(TrainCurrentState state) {
        if (state == null || state.getCurrentStation() == null) {
            throw new InvalidRouteDataException("Current station is missing in train current state.");
        }
        if (state.getProgressPercentage() == null
                || state.getProgressPercentage() < 0
                || state.getProgressPercentage() > 100) {
            throw new InvalidRouteDataException(
                    "Invalid progressPercentage: must be between 0 and 100, but was " + state.getProgressPercentage());
        }
        if (state.getCurrentDelayMinutes() == null || state.getCurrentDelayMinutes() < 0) {
            throw new InvalidRouteDataException(
                    "Invalid currentDelayMinutes: delay cannot be null or negative.");
        }
    }
}

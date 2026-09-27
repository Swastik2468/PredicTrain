package com.predictrack.service;

import com.predictrack.dto.CurrentStateResponse;
import com.predictrack.dto.DashboardResponse;
import com.predictrack.dto.TrainInfoResponse;
import com.predictrack.entity.Train;
import com.predictrack.entity.TrainCurrentState;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Orchestrates the dashboard data assembly for a train by invoking ETAService
 * and mapping entities into clean frontend DTOs.
 */
@Service
public class DashboardService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ETAService etaService;

    public DashboardService(ETAService etaService) {
        this.etaService = etaService;
    }

    /**
     * Dynamically calculates the ETA and assembles the full DashboardResponse for a train.
     */
    public DashboardResponse getTrainDashboard(String trainNumber) {
        LocalTime now = LocalTime.now();
        return getTrainDashboard(trainNumber, now);
    }

    /**
     * Overloaded method accepting a specific reference time (useful for deterministic testing).
     */
    public DashboardResponse getTrainDashboard(String trainNumber, LocalTime referenceTime) {
        ETAService.ETACalculationResult result = etaService.calculateETA(trainNumber, referenceTime);

        Train train = result.train();
        TrainCurrentState state = result.currentState();

        TrainInfoResponse trainInfo = new TrainInfoResponse(
                train.getTrainNumber(),
                train.getTrainName(),
                train.getSource(),
                train.getDestination()
        );

        String nextStationName = state.getNextStation() != null
                ? state.getNextStation().getStationName()
                : state.getCurrentStation().getStationName();

        String lastUpdatedFormatted = referenceTime.format(TIME_FORMATTER);

        CurrentStateResponse currentStateResponse = new CurrentStateResponse(
                state.getCurrentStation().getStationName(),
                nextStationName,
                state.getProgressPercentage(),
                state.getCurrentDelayMinutes(),
                lastUpdatedFormatted
        );

        return new DashboardResponse(
                trainInfo,
                currentStateResponse,
                result.eta(),
                result.delayBreakdown(),
                result.upcomingStations(),
                result.explanations()
        );
    }
}

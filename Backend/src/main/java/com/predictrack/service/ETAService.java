package com.predictrack.service;

import com.predictrack.dto.DelayBreakdownResponse;
import com.predictrack.dto.ETAResponse;
import com.predictrack.dto.ExplanationResponse;
import com.predictrack.dto.StationETAResponse;
import com.predictrack.entity.Station;
import com.predictrack.entity.Train;
import com.predictrack.entity.TrainCurrentState;
import com.predictrack.entity.TrainRoute;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Core ETA calculation engine for PredicTrack.
 *
 * Implements the layered dynamic forecast formula:
 *   ETA = CURRENT_TIME + REMAINING_JOURNEY_TIME
 * where:
 *   REMAINING_JOURNEY_TIME =
 *       REMAINING_RUNNING_TIME
 *     + REMAINING_DWELL_TIME
 *     + WEATHER_DELAY
 *     + INCIDENT_DELAY
 *     + OPTIONAL_ML_CORRECTION
 *
 * Also computes cumulative ETAs for every upcoming station along the remaining route,
 * applying segment running times, intermediate station dwell times, weather delays,
 * and active incident delays to the exact segments where they occur.
 */
@Service
public class ETAService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final TrainService trainService;
    private final RouteService routeService;
    private final HistoricalDataService historicalDataService;
    private final WeatherService weatherService;
    private final IncidentService incidentService;
    private final MLService mlService;

    public ETAService(
            TrainService trainService,
            RouteService routeService,
            HistoricalDataService historicalDataService,
            WeatherService weatherService,
            IncidentService incidentService,
            MLService mlService
    ) {
        this.trainService = trainService;
        this.routeService = routeService;
        this.historicalDataService = historicalDataService;
        this.weatherService = weatherService;
        this.incidentService = incidentService;
        this.mlService = mlService;
    }

    /**
     * Holds the complete result of an ETA calculation for a train.
     */
    public record ETACalculationResult(
            Train train,
            TrainCurrentState currentState,
            ETAResponse eta,
            DelayBreakdownResponse delayBreakdown,
            List<StationETAResponse> upcomingStations,
            List<ExplanationResponse> explanations
    ) {
    }

    /**
     * Calculates the dynamic ETA for a train using the current system clock (LocalTime.now()).
     */
    public ETACalculationResult calculateETA(String trainNumber) {
        return calculateETA(trainNumber, LocalTime.now());
    }

    /**
     * Calculates the dynamic ETA and cumulative upcoming station ETAs starting from referenceTime.
     */
    public ETACalculationResult calculateETA(String trainNumber, LocalTime referenceTime) {
        // Step 1: Find the train
        Train train = trainService.getTrainByNumber(trainNumber);

        // Step 2: Get TrainCurrentState
        TrainCurrentState currentState = trainService.getCurrentState(train);

        // Step 3: Get ordered route and slice from currentStation to final destination
        List<TrainRoute> orderedRoute = routeService.getOrderedRoute(train);
        List<TrainRoute> remainingRoute = routeService.getRemainingRouteFromCurrentStation(
                orderedRoute,
                currentState.getCurrentStation(),
                currentState.getNextStation()
        );

        Station finalDestinationStation = remainingRoute.get(remainingRoute.size() - 1).getStation();

        // Edge case: Train has already reached the final destination
        if (remainingRoute.size() <= 1) {
            String formattedNow = referenceTime.format(TIME_FORMATTER);
            ETAResponse etaResponse = new ETAResponse(
                    finalDestinationStation.getStationName(),
                    formattedNow,
                    formattedNow,
                    0
            );
            DelayBreakdownResponse breakdown = new DelayBreakdownResponse(0, 0, 0, 0, 0);
            return new ETACalculationResult(
                    train,
                    currentState,
                    etaResponse,
                    breakdown,
                    List.of(),
                    List.of()
            );
        }

        int totalRemainingRunningMinutes = 0;
        int totalRemainingDwellMinutes = 0;
        int totalWeatherDelayMinutes = 0;
        int totalIncidentDelayMinutes = 0;
        int cumulativeMinutes = 0;

        List<StationETAResponse> upcomingStations = new ArrayList<>();
        List<ExplanationResponse> explanations = new ArrayList<>();

        int lastSegmentIndex = remainingRoute.size() - 2;

        // Traverse each remaining segment (from index i -> index i + 1) cumulatively
        for (int i = 0; i <= lastSegmentIndex; i++) {
            Station fromStation = remainingRoute.get(i).getStation();
            Station toStation = remainingRoute.get(i + 1).getStation();

            // Step 4 & 5: Segment running time
            int medianSegmentMinutes = historicalDataService.getMedianRunningTimeMinutes(
                    train, fromStation, toStation
            );

            int segmentRunningMinutes;
            if (i == 0) {
                // Step 4: Current segment is partially completed according to progressPercentage
                double remainingFraction = 1.0 - (currentState.getProgressPercentage() / 100.0);
                segmentRunningMinutes = (int) Math.round(medianSegmentMinutes * remainingFraction);
            } else {
                // Step 5: Complete upcoming segment after the current segment
                segmentRunningMinutes = medianSegmentMinutes;
            }
            totalRemainingRunningMinutes += segmentRunningMinutes;

            // Step 7: Weather delay on this specific segment (fromStation -> toStation)
            WeatherService.SegmentWeatherImpact weatherImpact =
                    weatherService.evaluateSegmentWeather(fromStation, toStation);
            totalWeatherDelayMinutes += weatherImpact.delayMinutes();
            for (String msg : weatherImpact.explanations()) {
                explanations.add(new ExplanationResponse(msg));
            }

            // Step 8: Active incident delay on this specific segment (fromStation -> toStation)
            IncidentService.SegmentIncidentImpact incidentImpact =
                    incidentService.evaluateSegmentIncidents(fromStation, toStation);
            totalIncidentDelayMinutes += incidentImpact.delayMinutes();
            for (String msg : incidentImpact.explanations()) {
                explanations.add(new ExplanationResponse(msg));
            }

            // Advance cumulative clock to arrival at toStation
            cumulativeMinutes += segmentRunningMinutes
                    + weatherImpact.delayMinutes()
                    + incidentImpact.delayMinutes();

            // If this is the final segment, apply optional ML residual correction before recording destination arrival
            if (i == lastSegmentIndex) {
                int mlCorrection = mlService.getCorrection(
                        train,
                        currentState,
                        totalRemainingRunningMinutes,
                        totalRemainingDwellMinutes,
                        totalWeatherDelayMinutes,
                        totalIncidentDelayMinutes
                );
                cumulativeMinutes += mlCorrection;
            }

            // Record arrival ETA at toStation (before halting at toStation)
            LocalTime stationArrivalEta = referenceTime.plusMinutes(cumulativeMinutes);
            upcomingStations.add(new StationETAResponse(
                    toStation.getStationName(),
                    stationArrivalEta.format(TIME_FORMATTER)
            ));

            // Step 6: If toStation is an intermediate stop (not final destination),
            // add its median dwell time before departing for the next segment
            if (i < lastSegmentIndex) {
                int dwellMinutes = historicalDataService.getMedianDwellTimeMinutes(train, toStation);
                totalRemainingDwellMinutes += dwellMinutes;
                cumulativeMinutes += dwellMinutes;
            }
        }

        // Step 9: ML correction (0 when predictrack.ml.enabled=false)
        int mlCorrectionMinutes = mlService.getCorrection(
                train,
                currentState,
                totalRemainingRunningMinutes,
                totalRemainingDwellMinutes,
                totalWeatherDelayMinutes,
                totalIncidentDelayMinutes
        );

        // Baseline (Official IRCTC Scheduled Arrival without weather/incident delays)
        int baselineMinutes = totalRemainingRunningMinutes + totalRemainingDwellMinutes;
        LocalTime scheduledEtaTime = referenceTime.plusMinutes(baselineMinutes);

        // Step 10: Total remaining journey time
        int totalRemainingMinutes =
                baselineMinutes
                        + totalWeatherDelayMinutes
                        + totalIncidentDelayMinutes
                        + mlCorrectionMinutes;

        // Step 11: Final destination ETA = referenceTime + totalRemainingMinutes
        LocalTime finalEtaTime = referenceTime.plusMinutes(totalRemainingMinutes);

        ETAResponse etaResponse = new ETAResponse(
                finalDestinationStation.getStationName(),
                scheduledEtaTime.format(TIME_FORMATTER),
                finalEtaTime.format(TIME_FORMATTER),
                totalRemainingMinutes
        );

        DelayBreakdownResponse delayBreakdown = new DelayBreakdownResponse(
                totalRemainingRunningMinutes,
                totalRemainingDwellMinutes,
                totalWeatherDelayMinutes,
                totalIncidentDelayMinutes,
                mlCorrectionMinutes
        );

        return new ETACalculationResult(
                train,
                currentState,
                etaResponse,
                delayBreakdown,
                upcomingStations,
                explanations
        );
    }
}

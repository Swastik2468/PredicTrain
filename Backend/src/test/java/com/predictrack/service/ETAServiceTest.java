package com.predictrack.service;

import com.predictrack.dto.DashboardResponse;
import com.predictrack.dto.StationETAResponse;
import com.predictrack.entity.HistoricalDwell;
import com.predictrack.entity.HistoricalSegmentTime;
import com.predictrack.entity.Incident;
import com.predictrack.entity.IncidentSeverity;
import com.predictrack.entity.IncidentType;
import com.predictrack.entity.Station;
import com.predictrack.entity.Train;
import com.predictrack.entity.TrainCurrentState;
import com.predictrack.entity.TrainRoute;
import com.predictrack.entity.WeatherCondition;
import com.predictrack.entity.WeatherData;
import com.predictrack.provider.HistoricalDataProvider;
import com.predictrack.provider.IncidentProvider;
import com.predictrack.provider.TrainDataProvider;
import com.predictrack.provider.WeatherProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive unit/service integration tests for PredicTrack's ETA calculation engine.
 * Covers all 10 required scenarios from Section 20, with special emphasis on proving
 * that currentDelayMinutes is NOT double-counted in the final ETA.
 */
class ETAServiceTest {

    private Station mumbai;
    private Station surat;
    private Station bharuch;
    private Station vadodara;
    private Station anand;
    private Station ahmedabad;

    private Train train12901;
    private List<TrainRoute> route12901;
    private TrainCurrentState currentState12901;

    private Map<String, WeatherData> weatherByStationCode;
    private List<Incident> incidents;
    private Map<String, List<Integer>> segmentHistoryByPair;
    private Map<String, List<Integer>> dwellHistoryByStationCode;

    private ETAService etaService;
    private DashboardService dashboardService;

    private static final LocalTime REFERENCE_TIME = LocalTime.of(14, 5);

    @BeforeEach
    void setUp() {
        mumbai = new Station("MMCT", "Mumbai Central", 18.9696, 72.8193);
        surat = new Station("ST", "Surat", 21.2045, 72.8411);
        bharuch = new Station("BH", "Bharuch", 21.7051, 72.9959);
        vadodara = new Station("BRC", "Vadodara", 22.3106, 73.1812);
        anand = new Station("ANND", "Anand", 22.5586, 72.9626);
        ahmedabad = new Station("ADI", "Ahmedabad", 23.0268, 72.6012);

        train12901 = new Train("12901", "Mumbai-Ahmedabad Express", "Mumbai Central", "Ahmedabad");

        route12901 = List.of(
                new TrainRoute(train12901, mumbai, 1, LocalTime.of(9, 0), LocalTime.of(9, 0), 0, 0.0),
                new TrainRoute(train12901, surat, 2, LocalTime.of(12, 30), LocalTime.of(12, 35), 5, 263.0),
                new TrainRoute(train12901, bharuch, 3, LocalTime.of(13, 30), LocalTime.of(13, 33), 3, 59.0),
                new TrainRoute(train12901, vadodara, 4, LocalTime.of(14, 35), LocalTime.of(14, 40), 5, 71.0),
                new TrainRoute(train12901, anand, 5, LocalTime.of(15, 18), LocalTime.of(15, 20), 2, 36.0),
                new TrainRoute(train12901, ahmedabad, 6, LocalTime.of(16, 25), LocalTime.of(16, 25), 0, 64.0)
        );

        // Default current state: Surat -> Bharuch, 40% progress, 18 minutes delayed
        currentState12901 = new TrainCurrentState(
                train12901,
                surat,
                bharuch,
                40,
                18,
                LocalDateTime.of(LocalDate.now(), REFERENCE_TIME)
        );

        weatherByStationCode = new HashMap<>();
        incidents = new ArrayList<>();
        segmentHistoryByPair = new HashMap<>();
        dwellHistoryByStationCode = new HashMap<>();

        // Historical segment runs with variation around target medians:
        // Surat -> Bharuch: median 55 min (52, 57, 54, 56, 53, 58, 55, 54, 56)
        segmentHistoryByPair.put("ST->BH", List.of(52, 57, 54, 56, 53, 58, 55, 54, 56));
        // Bharuch -> Vadodara: median 62 min
        segmentHistoryByPair.put("BH->BRC", List.of(59, 64, 61, 63, 60, 65, 62, 61, 63));
        // Vadodara -> Anand: median 38 min
        segmentHistoryByPair.put("BRC->ANND", List.of(35, 40, 37, 39, 36, 41, 38, 37, 39));
        // Anand -> Ahmedabad: median 65 min
        segmentHistoryByPair.put("ANND->ADI", List.of(62, 67, 64, 66, 63, 68, 65, 64, 66));

        // Historical dwell times with variation around target medians:
        // Bharuch = 3 min, Vadodara = 5 min, Anand = 2 min
        dwellHistoryByStationCode.put("BH", List.of(2, 4, 3, 3, 5, 2, 3));
        dwellHistoryByStationCode.put("BRC", List.of(4, 6, 5, 5, 7, 4, 5));
        dwellHistoryByStationCode.put("ANND", List.of(1, 3, 2, 2, 4, 1, 2));

        // Wire services using lightweight in-memory test providers
        TrainDataProvider trainDataProvider = new TrainDataProvider() {
            @Override
            public Optional<Train> findTrainByNumber(String trainNumber) {
                return "12901".equals(trainNumber) ? Optional.of(train12901) : Optional.empty();
            }

            @Override
            public List<TrainRoute> findOrderedRouteByTrain(Train train) {
                return route12901;
            }

            @Override
            public Optional<TrainCurrentState> findCurrentStateByTrain(Train train) {
                return Optional.of(currentState12901);
            }
        };

        HistoricalDataProvider historicalDataProvider = new HistoricalDataProvider() {
            @Override
            public List<HistoricalSegmentTime> findSegmentHistory(Train train, Station from, Station to) {
                String key = from.getStationCode() + "->" + to.getStationCode();
                List<Integer> runs = segmentHistoryByPair.getOrDefault(key, List.of());
                List<HistoricalSegmentTime> list = new ArrayList<>();
                for (int r : runs) {
                    list.add(new HistoricalSegmentTime(
                            train, from, to, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(10, 0).plusMinutes(r), r
                    ));
                }
                return list;
            }

            @Override
            public List<HistoricalDwell> findDwellHistory(Train train, Station station) {
                List<Integer> dwells = dwellHistoryByStationCode.getOrDefault(station.getStationCode(), List.of());
                List<HistoricalDwell> list = new ArrayList<>();
                for (int d : dwells) {
                    list.add(new HistoricalDwell(
                            train, station, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(10, 0).plusMinutes(d), d
                    ));
                }
                return list;
            }
        };

        WeatherProvider weatherProvider = station ->
                Optional.ofNullable(weatherByStationCode.get(station.getStationCode()));

        IncidentProvider incidentProvider = (from, to) -> {
            List<Incident> matched = new ArrayList<>();
            for (Incident inc : incidents) {
                if (inc.getFromStation().getStationCode().equals(from.getStationCode())
                        && inc.getToStation().getStationCode().equals(to.getStationCode())
                        && Boolean.TRUE.equals(inc.getActive())) {
                    matched.add(inc);
                }
            }
            return matched;
        };

        TrainService trainService = new TrainService(trainDataProvider);
        RouteService routeService = new RouteService(trainDataProvider);
        HistoricalDataService historicalDataService = new HistoricalDataService(historicalDataProvider);
        WeatherService weatherService = new WeatherService(weatherProvider);
        IncidentService incidentService = new IncidentService(incidentProvider);
        MLService mlService = new DefaultMLService(false); // ML disabled

        etaService = new ETAService(
                trainService,
                routeService,
                historicalDataService,
                weatherService,
                incidentService,
                mlService
        );
        dashboardService = new DashboardService(etaService);
    }

    @Test
    @DisplayName("1. Normal train with no weather or incidents")
    void testNormalTrainWithNoWeatherOrIncidents() {
        currentState12901.setCurrentDelayMinutes(0);

        ETAService.ETACalculationResult result = etaService.calculateETA("12901", REFERENCE_TIME);

        // Running time: 33 (ST->BH remaining) + 62 (BH->BRC) + 38 (BRC->ANND) + 65 (ANND->ADI) = 198 min
        // Dwell time: 3 (BH) + 5 (BRC) + 2 (ANND) = 10 min
        // Weather = 0, Incident = 0, ML = 0 -> Total remaining = 208 min
        assertEquals(198, result.delayBreakdown().runningTimeMinutes());
        assertEquals(10, result.delayBreakdown().stationDwellMinutes());
        assertEquals(0, result.delayBreakdown().weatherDelayMinutes());
        assertEquals(0, result.delayBreakdown().incidentDelayMinutes());
        assertEquals(0, result.delayBreakdown().mlCorrectionMinutes());
        assertEquals(208, result.eta().remainingMinutes());
        assertEquals("17:33", result.eta().estimatedArrival());
        assertTrue(result.explanations().isEmpty());
    }

    @Test
    @DisplayName("2. Train currently delayed - currentDelayMinutes is NOT double-counted")
    void testTrainCurrentlyDelayedDoesNotDoubleCountDelay() {
        // Run 1: Train at Surat->Bharuch (40% progress) with 0 min delay
        currentState12901.setCurrentDelayMinutes(0);
        DashboardResponse onTimeDashboard = dashboardService.getTrainDashboard("12901", REFERENCE_TIME);

        // Run 2: Train at the EXACT SAME position (Surat->Bharuch, 40% progress) with 18 min delay
        currentState12901.setCurrentDelayMinutes(18);
        DashboardResponse delayedDashboard = dashboardService.getTrainDashboard("12901", REFERENCE_TIME);

        // Current delay is reported in currentState for the frontend dashboard...
        assertEquals(18, delayedDashboard.currentState().currentDelayMinutes());

        // ...but is NOT added again to remainingMinutes or estimatedArrival!
        assertEquals(onTimeDashboard.eta().remainingMinutes(), delayedDashboard.eta().remainingMinutes());
        assertEquals(onTimeDashboard.eta().estimatedArrival(), delayedDashboard.eta().estimatedArrival());
    }

    @Test
    @DisplayName("3. Heavy weather on remaining route adds weather delay and explanations")
    void testHeavyWeatherOnRemainingRoute() {
        weatherByStationCode.put("BRC", new WeatherData(
                vadodara, LocalDateTime.now(), 25.0, 32.0, 2.5, 28.0, WeatherCondition.HEAVY_RAIN
        ));
        weatherByStationCode.put("ANND", new WeatherData(
                anand, LocalDateTime.now(), 26.5, 14.0, 5.0, 18.0, WeatherCondition.MODERATE_RAIN
        ));

        ETAService.ETACalculationResult result = etaService.calculateETA("12901", REFERENCE_TIME);

        // Heavy rain at Vadodara (+8) + Moderate rain at Anand (+3) = 11 min
        assertEquals(11, result.delayBreakdown().weatherDelayMinutes());
        assertEquals(208 + 11, result.eta().remainingMinutes());
        assertEquals(2, result.explanations().size());
    }

    @Test
    @DisplayName("4. Active congestion on remaining route adds incident delay")
    void testActiveCongestionOnRemainingRoute() {
        incidents.add(new Incident(
                IncidentType.CONGESTION,
                bharuch,
                vadodara,
                IncidentSeverity.MEDIUM,
                12,
                "Congestion between Bharuch and Vadodara may add approximately 12 minutes.",
                true,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2)
        ));

        ETAService.ETACalculationResult result = etaService.calculateETA("12901", REFERENCE_TIME);

        assertEquals(12, result.delayBreakdown().incidentDelayMinutes());
        assertEquals(208 + 12, result.eta().remainingMinutes());
        assertEquals(1, result.explanations().size());
        assertEquals(
                "Congestion between Bharuch and Vadodara may add approximately 12 minutes.",
                result.explanations().get(0).message()
        );
    }

    @Test
    @DisplayName("5. Weather + incident together")
    void testWeatherAndIncidentTogether() {
        weatherByStationCode.put("BRC", new WeatherData(
                vadodara, LocalDateTime.now(), 25.0, 32.0, 2.5, 28.0, WeatherCondition.HEAVY_RAIN
        ));
        weatherByStationCode.put("ANND", new WeatherData(
                anand, LocalDateTime.now(), 26.5, 14.0, 5.0, 18.0, WeatherCondition.MODERATE_RAIN
        ));
        incidents.add(new Incident(
                IncidentType.CONGESTION,
                bharuch,
                vadodara,
                IncidentSeverity.MEDIUM,
                12,
                "Congestion between Bharuch and Vadodara may add approximately 12 minutes.",
                true,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2)
        ));

        ETAService.ETACalculationResult result = etaService.calculateETA("12901", REFERENCE_TIME);

        // 198 running + 10 dwell + 11 weather + 12 incident = 231 min
        assertEquals(198, result.delayBreakdown().runningTimeMinutes());
        assertEquals(10, result.delayBreakdown().stationDwellMinutes());
        assertEquals(11, result.delayBreakdown().weatherDelayMinutes());
        assertEquals(12, result.delayBreakdown().incidentDelayMinutes());
        assertEquals(231, result.eta().remainingMinutes());
        assertEquals(3, result.explanations().size());
    }

    @Test
    @DisplayName("6. Train partially through a segment scales current segment time accurately")
    void testTrainPartiallyThroughSegment() {
        // At 40% progress on 55-min Surat->Bharuch segment: 55 * (1 - 0.40) = 33 min
        currentState12901.setProgressPercentage(40);
        ETAService.ETACalculationResult resultAt40 = etaService.calculateETA("12901", REFERENCE_TIME);
        assertEquals("14:38", resultAt40.upcomingStations().get(0).eta()); // 14:05 + 33 min = 14:38

        // At 80% progress on 55-min Surat->Bharuch segment: 55 * (1 - 0.80) = 11 min
        currentState12901.setProgressPercentage(80);
        ETAService.ETACalculationResult resultAt80 = etaService.calculateETA("12901", REFERENCE_TIME);
        assertEquals("14:16", resultAt80.upcomingStations().get(0).eta()); // 14:05 + 11 min = 14:16
    }

    @Test
    @DisplayName("7. Final destination ETA matches referenceTime + remainingMinutes")
    void testFinalDestinationETA() {
        // Set Anand->Ahmedabad median to 43 min so total running time = 33 + 62 + 38 + 43 = 176 min
        // matching the exact example numbers in Section 14 (176 + 10 + 11 + 12 = 209 or 199 min)
        segmentHistoryByPair.put("ANND->ADI", List.of(40, 45, 42, 44, 41, 46, 43, 42, 44));
        weatherByStationCode.put("BRC", new WeatherData(
                vadodara, LocalDateTime.now(), 25.0, 32.0, 2.5, 28.0, WeatherCondition.HEAVY_RAIN
        ));
        weatherByStationCode.put("ANND", new WeatherData(
                anand, LocalDateTime.now(), 26.5, 14.0, 5.0, 18.0, WeatherCondition.MODERATE_RAIN
        ));
        incidents.add(new Incident(
                IncidentType.CONGESTION,
                bharuch,
                vadodara,
                IncidentSeverity.MEDIUM,
                12,
                "Congestion between Bharuch and Vadodara may add approximately 12 minutes.",
                true,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2)
        ));

        ETAService.ETACalculationResult result = etaService.calculateETA("12901", REFERENCE_TIME);

        assertEquals("Ahmedabad", result.eta().destination());
        assertEquals(176, result.delayBreakdown().runningTimeMinutes());
        assertEquals(10, result.delayBreakdown().stationDwellMinutes());
        assertEquals(11, result.delayBreakdown().weatherDelayMinutes());
        assertEquals(12, result.delayBreakdown().incidentDelayMinutes());
        assertEquals(209, result.eta().remainingMinutes());
        assertEquals("17:34", result.eta().estimatedArrival());
    }

    @Test
    @DisplayName("8. Upcoming station ETAs are calculated cumulatively along the remaining route")
    void testUpcomingStationETAs() {
        weatherByStationCode.put("BRC", new WeatherData(
                vadodara, LocalDateTime.now(), 25.0, 32.0, 2.5, 28.0, WeatherCondition.HEAVY_RAIN
        ));
        weatherByStationCode.put("ANND", new WeatherData(
                anand, LocalDateTime.now(), 26.5, 14.0, 5.0, 18.0, WeatherCondition.MODERATE_RAIN
        ));
        incidents.add(new Incident(
                IncidentType.CONGESTION,
                bharuch,
                vadodara,
                IncidentSeverity.MEDIUM,
                12,
                "Congestion between Bharuch and Vadodara may add approximately 12 minutes.",
                true,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2)
        ));

        ETAService.ETACalculationResult result = etaService.calculateETA("12901", REFERENCE_TIME);
        List<StationETAResponse> stations = result.upcomingStations();

        assertEquals(4, stations.size());
        // 1) Bharuch: 14:05 + 33m = 14:38
        assertEquals("Bharuch", stations.get(0).station());
        assertEquals("14:38", stations.get(0).eta());

        // 2) Vadodara: 14:38 + 3m (BH dwell) + 62m (run) + 8m (rain) + 12m (congestion) = 16:03
        assertEquals("Vadodara", stations.get(1).station());
        assertEquals("16:03", stations.get(1).eta());

        // 3) Anand: 16:03 + 5m (BRC dwell) + 38m (run) + 3m (rain) = 16:49
        assertEquals("Anand", stations.get(2).station());
        assertEquals("16:49", stations.get(2).eta());

        // 4) Ahmedabad: 16:49 + 2m (ANND dwell) + 65m (run) = 17:56
        assertEquals("Ahmedabad", stations.get(3).station());
        assertEquals("17:56", stations.get(3).eta());
    }

    @Test
    @DisplayName("9. Train not found throws TrainNotFoundException and invalid number throws InvalidTrainNumberException")
    void testTrainNotFoundAndInvalidTrainNumber() {
        assertThrows(
                TrainNotFoundException.class,
                () -> etaService.calculateETA("99999", REFERENCE_TIME)
        );

        assertThrows(
                InvalidTrainNumberException.class,
                () -> etaService.calculateETA("INVALID_ABC", REFERENCE_TIME)
        );
    }

    @Test
    @DisplayName("10. No active incidents - inactive incidents are ignored")
    void testNoActiveIncidentsIgnoresInactiveIncidents() {
        // Add an INACTIVE incident on Bharuch -> Vadodara
        incidents.add(new Incident(
                IncidentType.TRACK_FAULT,
                bharuch,
                vadodara,
                IncidentSeverity.HIGH,
                45,
                "Resolved track fault between Bharuch and Vadodara.",
                false, // active = false
                LocalDateTime.now().minusHours(5),
                LocalDateTime.now().minusHours(2)
        ));

        ETAService.ETACalculationResult result = etaService.calculateETA("12901", REFERENCE_TIME);

        assertEquals(0, result.delayBreakdown().incidentDelayMinutes());
        assertTrue(result.explanations().isEmpty());
    }
}

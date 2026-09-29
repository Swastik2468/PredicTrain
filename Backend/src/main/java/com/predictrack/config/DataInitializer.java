package com.predictrack.config;

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
import com.predictrack.repository.HistoricalDwellRepository;
import com.predictrack.repository.HistoricalSegmentTimeRepository;
import com.predictrack.repository.IncidentRepository;
import com.predictrack.repository.StationRepository;
import com.predictrack.repository.TrainCurrentStateRepository;
import com.predictrack.repository.TrainRepository;
import com.predictrack.repository.TrainRouteRepository;
import com.predictrack.repository.WeatherDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Seeds the MySQL database with realistic synthetic data for 6 coaching trains,
 * their 8-10 station routes, historical segment running times, historical dwell times,
 * station weather conditions, and operational incidents.
 *
 * Idempotent: Checks if trains already exist before inserting, so restarting the
 * Spring Boot application will NEVER duplicate rows.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final TrainRouteRepository trainRouteRepository;
    private final TrainCurrentStateRepository trainCurrentStateRepository;
    private final HistoricalSegmentTimeRepository historicalSegmentTimeRepository;
    private final HistoricalDwellRepository historicalDwellRepository;
    private final WeatherDataRepository weatherDataRepository;
    private final IncidentRepository incidentRepository;

    public DataInitializer(
            StationRepository stationRepository,
            TrainRepository trainRepository,
            TrainRouteRepository trainRouteRepository,
            TrainCurrentStateRepository trainCurrentStateRepository,
            HistoricalSegmentTimeRepository historicalSegmentTimeRepository,
            HistoricalDwellRepository historicalDwellRepository,
            WeatherDataRepository weatherDataRepository,
            IncidentRepository incidentRepository
    ) {
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.trainRouteRepository = trainRouteRepository;
        this.trainCurrentStateRepository = trainCurrentStateRepository;
        this.historicalSegmentTimeRepository = historicalSegmentTimeRepository;
        this.historicalDwellRepository = historicalDwellRepository;
        this.weatherDataRepository = weatherDataRepository;
        this.incidentRepository = incidentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (trainRepository.count() > 0) {
            log.info("PredicTrack database already contains mock data. Skipping initialization.");
            return;
        }

        log.info("Initializing PredicTrack database with synthetic railway data for 6 trains...");

        Map<String, Station> stations = seedStations();
        seedWeather(stations);
        seedTrainsAndRoutesAndHistory(stations);
        seedIncidents(stations);

        log.info("PredicTrack mock data initialization complete.");
    }

    private Map<String, Station> seedStations() {
        List<Station> stationList = List.of(
                // Corridor 1: Mumbai - Ahmedabad (Train 12901) & Rajdhani (Train 12951)
                new Station("MMCT", "Mumbai Central", 18.9696, 72.8193),
                new Station("DDR", "Dadar", 19.0178, 72.8478),
                new Station("BVI", "Borivali", 19.2288, 72.8567),
                new Station("VAPI", "Vapi", 20.3717, 72.9049),
                new Station("BL", "Valsad", 20.6100, 72.9342),
                new Station("ST", "Surat", 21.2045, 72.8411),
                new Station("BH", "Bharuch", 21.7051, 72.9959),
                new Station("BRC", "Vadodara", 22.3106, 73.1812),
                new Station("ANND", "Anand", 22.5586, 72.9626),
                new Station("ADI", "Ahmedabad", 23.0268, 72.6012),

                // Northern extension for 12951 Rajdhani & 12002 Shatabdi
                new Station("RTM", "Ratlam", 23.3342, 75.0446),
                new Station("KOTA", "Kota", 25.2238, 75.8806),
                new Station("SWM", "Sawai Madhopur", 26.0173, 76.3526),
                new Station("MTJ", "Mathura", 27.4795, 77.6736),
                new Station("NDLS", "New Delhi", 28.6429, 77.2191),
                new Station("AGC", "Agra Cantt", 27.1578, 77.9895),
                new Station("DHO", "Dholpur", 26.6975, 77.8869),
                new Station("MRA", "Morena", 26.4947, 77.9940),
                new Station("GWL", "Gwalior", 26.2154, 78.1822),
                new Station("VGLJ", "Jhansi", 25.4484, 78.5565),
                new Station("BINA", "Bina", 24.1780, 78.1850),
                new Station("RKMP", "Rani Kamalapati", 23.2200, 77.4385),

                // Corridor 4: Karnataka Express (12627)
                new Station("SBC", "KSR Bengaluru", 12.9781, 77.5696),
                new Station("DMM", "Dharmavaram", 14.4141, 77.7126),
                new Station("ATP", "Anantapur", 14.6819, 77.6006),
                new Station("GTL", "Guntakal", 15.1726, 77.3659),
                new Station("WADI", "Wadi", 17.0543, 76.9937),
                new Station("SUR", "Solapur", 17.6615, 75.8944),
                new Station("DD", "Daund", 18.4631, 74.5836),
                new Station("ANG", "Ahmadnagar", 19.0760, 74.7390),
                new Station("MMR", "Manmad", 20.2481, 74.4366),

                // Corridor 5: Coromandel Express (12841)
                new Station("SHM", "Shalimar", 22.5556, 88.3168),
                new Station("KGP", "Kharagpur", 22.3420, 87.3247),
                new Station("BLS", "Balasore", 21.5008, 86.9204),
                new Station("CTC", "Cuttack", 20.4637, 85.9001),
                new Station("BBS", "Bhubaneswar", 20.2667, 85.8436),
                new Station("BAM", "Brahmapur", 19.3075, 84.8017),
                new Station("VSKP", "Visakhapatnam", 17.7222, 83.2897),
                new Station("RJY", "Rajahmundry", 16.9891, 81.7840),
                new Station("BZA", "Vijayawada", 16.5186, 80.6199),
                new Station("MAS", "MGR Chennai Central", 13.0827, 80.2750),

                // Corridor 6: Sealdah - New Delhi Duronto (12259)
                new Station("SDAH", "Sealdah", 22.5674, 88.3708),
                new Station("ASN", "Asansol", 23.6889, 86.9661),
                new Station("DHN", "Dhanbad", 23.7907, 86.4300),
                new Station("PNME", "Parasnath", 23.9789, 86.1408),
                new Station("GAYA", "Gaya", 24.8037, 84.9994),
                new Station("DDU", "Pt DD Upadhyaya", 25.2797, 83.1197),
                new Station("PRYJ", "Prayagraj", 25.4448, 81.8273),
                new Station("CNB", "Kanpur Central", 26.4539, 80.3512),
                new Station("ETW", "Etawah", 26.7850, 79.0214)
        );

        stationRepository.saveAll(stationList);
        Map<String, Station> map = new HashMap<>();
        for (Station s : stationList) {
            map.put(s.getStationCode(), s);
        }
        return map;
    }

    private void seedWeather(Map<String, Station> stations) {
        LocalDateTime now = LocalDateTime.now();
        List<WeatherData> weatherList = new ArrayList<>();

        for (Station station : stations.values()) {
            String code = station.getStationCode();
            WeatherCondition condition = WeatherCondition.CLEAR;
            double temp = 30.0;
            double rain = 0.0;
            double visibility = 10.0;
            double wind = 12.0;

            // Specific weather conditions to demonstrate realistic weather delays
            switch (code) {
                case "BRC" -> { // Vadodara: Heavy rain (+8 min)
                    condition = WeatherCondition.HEAVY_RAIN;
                    temp = 25.0;
                    rain = 32.0;
                    visibility = 2.5;
                    wind = 28.0;
                }
                case "ANND" -> { // Anand: Moderate rain (+3 min)
                    condition = WeatherCondition.MODERATE_RAIN;
                    temp = 26.5;
                    rain = 14.0;
                    visibility = 5.0;
                    wind = 18.0;
                }
                case "MTJ" -> { // Mathura: Fog (+6 min)
                    condition = WeatherCondition.FOG;
                    temp = 19.0;
                    rain = 0.0;
                    visibility = 0.8;
                    wind = 6.0;
                }
                case "VSKP" -> { // Visakhapatnam: Storm (+10 min)
                    condition = WeatherCondition.STORM;
                    temp = 24.0;
                    rain = 45.0;
                    visibility = 1.5;
                    wind = 48.0;
                }
                case "SUR" -> { // Solapur: Light rain (+2 min)
                    condition = WeatherCondition.LIGHT_RAIN;
                    temp = 27.0;
                    rain = 4.5;
                    visibility = 7.0;
                    wind = 15.0;
                }
                default -> {
                    condition = WeatherCondition.CLEAR;
                }
            }

            weatherList.add(new WeatherData(station, now, temp, rain, visibility, wind, condition));
        }

        weatherDataRepository.saveAll(weatherList);
    }

    private void seedTrainsAndRoutesAndHistory(Map<String, Station> s) {
        LocalDateTime now = LocalDateTime.now();

        // =========================================================================
        // Train 1: 12901 - Mumbai-Ahmedabad Express (Primary Example Train)
        // 10 stations: MMCT -> DDR -> BVI -> VAPI -> BL -> ST -> BH -> BRC -> ANND -> ADI
        // Currently between Surat (ST) and Bharuch (BH), 40% progress, 18 min late
        // Remaining segments:
        //   ST -> BH:   median 55 min (40% done -> 33 min remaining)
        //   BH -> BRC:  median 62 min
        //   BRC -> ANND: median 38 min
        //   ANND -> ADI: median 65 min
        // Remaining station dwells:
        //   BH = 3 min, BRC = 5 min, ANND = 2 min (total 10 min)
        // =========================================================================
        Train t12901 = new Train("12901", "Mumbai-Ahmedabad Express", "Mumbai Central", "Ahmedabad");
        trainRepository.save(t12901);

        String[] r12901Codes = {"MMCT", "DDR", "BVI", "VAPI", "BL", "ST", "BH", "BRC", "ANND", "ADI"};
        int[] r12901SegMedians = {12, 24, 95, 25, 50, 55, 62, 38, 65};
        int[] r12901DwellMedians = {0, 2, 3, 2, 2, 5, 3, 5, 2, 0};
        double[] r12901Distances = {0.0, 6.0, 24.0, 140.0, 24.0, 69.0, 59.0, 71.0, 36.0, 64.0};

        seedSingleTrainDetails(
                t12901,
                LocalTime.of(9, 0),
                r12901Codes,
                r12901SegMedians,
                r12901DwellMedians,
                r12901Distances,
                s.get("ST"),
                s.get("BH"),
                40,
                23,
                now,
                s
        );

        // =========================================================================
        // Train 2: 12951 - Mumbai-New Delhi Rajdhani Express
        // 9 stations: MMCT -> BVI -> ST -> BRC -> RTM -> KOTA -> SWM -> MTJ -> NDLS
        // =========================================================================
        Train t12951 = new Train("12951", "Mumbai-New Delhi Rajdhani Express", "Mumbai Central", "New Delhi");
        trainRepository.save(t12951);

        String[] r12951Codes = {"MMCT", "BVI", "ST", "BRC", "RTM", "KOTA", "SWM", "MTJ", "NDLS"};
        int[] r12951SegMedians = {25, 140, 90, 185, 175, 65, 130, 100};
        int[] r12951DwellMedians = {0, 2, 5, 5, 3, 10, 2, 2, 0};
        double[] r12951Distances = {0.0, 30.0, 233.0, 130.0, 261.0, 267.0, 108.0, 216.0, 141.0};

        seedSingleTrainDetails(
                t12951,
                LocalTime.of(17, 0),
                r12951Codes,
                r12951SegMedians,
                r12951DwellMedians,
                r12951Distances,
                s.get("RTM"),
                s.get("KOTA"),
                50,
                6,
                now,
                s
        );

        // =========================================================================
        // Train 3: 12002 - New Delhi-Bhopal Shatabdi Express
        // 9 stations: NDLS -> MTJ -> AGC -> DHO -> MRA -> GWL -> VGLJ -> BINA -> RKMP
        // =========================================================================
        Train t12002 = new Train("12002", "New Delhi-Bhopal Shatabdi Express", "New Delhi", "Rani Kamalapati");
        trainRepository.save(t12002);

        String[] r12002Codes = {"NDLS", "MTJ", "AGC", "DHO", "MRA", "GWL", "VGLJ", "BINA", "RKMP"};
        int[] r12002SegMedians = {90, 38, 35, 22, 30, 68, 95, 90};
        int[] r12002DwellMedians = {0, 2, 5, 2, 2, 5, 8, 5, 0};
        double[] r12002Distances = {0.0, 141.0, 54.0, 52.0, 28.0, 39.0, 98.0, 153.0, 139.0};

        seedSingleTrainDetails(
                t12002,
                LocalTime.of(6, 0),
                r12002Codes,
                r12002SegMedians,
                r12002DwellMedians,
                r12002Distances,
                s.get("AGC"),
                s.get("DHO"),
                25,
                15,
                now,
                s
        );

        // =========================================================================
        // Train 4: 12627 - Karnataka Express
        // 9 stations: SBC -> DMM -> ATP -> GTL -> WADI -> SUR -> DD -> ANG -> MMR
        // =========================================================================
        Train t12627 = new Train("12627", "Karnataka Express", "KSR Bengaluru", "Manmad");
        trainRepository.save(t12627);

        String[] r12627Codes = {"SBC", "DMM", "ATP", "GTL", "WADI", "SUR", "DD", "ANG", "MMR"};
        int[] r12627SegMedians = {170, 32, 60, 180, 130, 165, 80, 110};
        int[] r12627DwellMedians = {0, 5, 2, 10, 5, 5, 5, 3, 0};
        double[] r12627Distances = {0.0, 179.0, 34.0, 68.0, 229.0, 150.0, 188.0, 84.0, 125.0};

        seedSingleTrainDetails(
                t12627,
                LocalTime.of(19, 20),
                r12627Codes,
                r12627SegMedians,
                r12627DwellMedians,
                r12627Distances,
                s.get("WADI"),
                s.get("SUR"),
                60,
                2,
                now,
                s
        );

        // =========================================================================
        // Train 5: 12841 - Coromandel Express
        // 10 stations: SHM -> KGP -> BLS -> CTC -> BBS -> BAM -> VSKP -> RJY -> BZA -> MAS
        // =========================================================================
        Train t12841 = new Train("12841", "Coromandel Express", "Shalimar", "MGR Chennai Central");
        trainRepository.save(t12841);

        String[] r12841Codes = {"SHM", "KGP", "BLS", "CTC", "BBS", "BAM", "VSKP", "RJY", "BZA", "MAS"};
        int[] r12841SegMedians = {105, 95, 135, 30, 125, 210, 155, 120, 340};
        int[] r12841DwellMedians = {0, 5, 2, 2, 5, 5, 15, 3, 10, 0};
        double[] r12841Distances = {0.0, 115.0, 118.0, 179.0, 28.0, 166.0, 277.0, 201.0, 149.0, 431.0};

        seedSingleTrainDetails(
                t12841,
                LocalTime.of(15, 20),
                r12841Codes,
                r12841SegMedians,
                r12841DwellMedians,
                r12841Distances,
                s.get("BBS"),
                s.get("BAM"),
                30,
                20,
                now,
                s
        );

        // =========================================================================
        // Train 6: 12259 - Sealdah-New Delhi Duronto Express (Clean run: no weather/incidents)
        // 10 stations: SDAH -> ASN -> DHN -> PNME -> GAYA -> DDU -> PRYJ -> CNB -> ETW -> NDLS
        // =========================================================================
        Train t12259 = new Train("12259", "Sealdah-New Delhi Duronto Express", "Sealdah", "New Delhi");
        trainRepository.save(t12259);

        String[] r12259Codes = {"SDAH", "ASN", "DHN", "PNME", "GAYA", "DDU", "PRYJ", "CNB", "ETW", "NDLS"};
        int[] r12259SegMedians = {150, 50, 40, 95, 130, 105, 130, 85, 195};
        int[] r12259DwellMedians = {0, 3, 5, 2, 3, 10, 5, 5, 2, 0};
        double[] r12259Distances = {0.0, 213.0, 59.0, 48.0, 152.0, 203.0, 153.0, 194.0, 139.0, 296.0};

        seedSingleTrainDetails(
                t12259,
                LocalTime.of(18, 30),
                r12259Codes,
                r12259SegMedians,
                r12259DwellMedians,
                r12259Distances,
                s.get("DDU"),
                s.get("PRYJ"),
                50,
                0,
                now,
                s
        );
    }

    private void seedSingleTrainDetails(
            Train train,
            LocalTime startDeparture,
            String[] stationCodes,
            int[] segmentMedians,
            int[] dwellMedians,
            double[] distances,
            Station currentStation,
            Station nextStation,
            int progressPercentage,
            int currentDelayMinutes,
            LocalDateTime now,
            Map<String, Station> stationMap
    ) {
        List<TrainRoute> routes = new ArrayList<>();
        List<HistoricalSegmentTime> segmentHistory = new ArrayList<>();
        List<HistoricalDwell> dwellHistory = new ArrayList<>();

        LocalTime cursorTime = startDeparture;
        LocalDate baseDate = LocalDate.now().minusDays(10);

        // Offsets around median (9 values whose sorted middle value is 0 -> exact median preserved!)
        // For example, if median = 55, this generates: 52, 57, 54, 56, 53, 58, 55, 54, 56
        int[] segmentOffsets = {-3, +2, -1, +1, -2, +3, 0, -1, +1};
        // Offsets for dwell (7 values whose sorted middle value is 0 -> exact median preserved!)
        int[] dwellOffsets = {-1, +1, 0, 0, +2, -1, 0};

        for (int i = 0; i < stationCodes.length; i++) {
            Station station = stationMap.get(stationCodes[i]);
            int halt = dwellMedians[i];
            LocalTime arrival = cursorTime;
            LocalTime departure = cursorTime.plusMinutes(halt);

            routes.add(new TrainRoute(
                    train,
                    station,
                    i + 1,
                    arrival,
                    departure,
                    halt,
                    distances[i]
            ));

            // Seed 7 historical dwell records for intermediate stations
            if (i > 0 && i < stationCodes.length - 1) {
                for (int d = 0; d < dwellOffsets.length; d++) {
                    int actualDwell = Math.max(1, halt + dwellOffsets[d]);
                    LocalDate journeyDate = baseDate.plusDays(d);
                    dwellHistory.add(new HistoricalDwell(
                            train,
                            station,
                            journeyDate,
                            arrival,
                            arrival.plusMinutes(actualDwell),
                            actualDwell
                    ));
                }
            }

            // Seed 9 historical segment time records for segment (i -> i+1)
            if (i < stationCodes.length - 1) {
                Station toStation = stationMap.get(stationCodes[i + 1]);
                int medianRun = segmentMedians[i];
                for (int h = 0; h < segmentOffsets.length; h++) {
                    int actualRun = Math.max(5, medianRun + segmentOffsets[h]);
                    LocalDate journeyDate = baseDate.plusDays(h);
                    segmentHistory.add(new HistoricalSegmentTime(
                            train,
                            station,
                            toStation,
                            journeyDate,
                            departure,
                            departure.plusMinutes(actualRun),
                            actualRun
                    ));
                }
                cursorTime = departure.plusMinutes(medianRun);
            }
        }

        trainRouteRepository.saveAll(routes);
        historicalSegmentTimeRepository.saveAll(segmentHistory);
        historicalDwellRepository.saveAll(dwellHistory);

        TrainCurrentState currentState = new TrainCurrentState(
                train,
                currentStation,
                nextStation,
                progressPercentage,
                currentDelayMinutes,
                now
        );
        trainCurrentStateRepository.save(currentState);
    }

    private void seedIncidents(Map<String, Station> s) {
        LocalDateTime now = LocalDateTime.now();

        List<Incident> incidents = List.of(
                // 1. Active congestion on 12901's remaining route: Bharuch -> Vadodara (+12 min)
                new Incident(
                        IncidentType.CONGESTION,
                        s.get("BH"),
                        s.get("BRC"),
                        IncidentSeverity.MEDIUM,
                        12,
                        "Congestion between Bharuch and Vadodara may add approximately 12 minutes.",
                        true,
                        now.minusHours(1),
                        now.plusHours(3)
                ),
                // 2. Inactive incident on 12901's route (already cleared -> should NOT affect ETA)
                new Incident(
                        IncidentType.TRACK_FAULT,
                        s.get("ANND"),
                        s.get("ADI"),
                        IncidentSeverity.LOW,
                        15,
                        "Resolved track maintenance between Anand and Ahmedabad.",
                        false,
                        now.minusHours(5),
                        now.minusHours(2)
                ),
                // 3. Active signal failure on 12002's remaining route: Gwalior -> Jhansi (+15 min)
                new Incident(
                        IncidentType.SIGNAL_FAILURE,
                        s.get("GWL"),
                        s.get("VGLJ"),
                        IncidentSeverity.HIGH,
                        15,
                        "Signal failure between Gwalior and Jhansi may add approximately 15 minutes.",
                        true,
                        now.minusMinutes(45),
                        now.plusHours(2)
                ),
                // 4. Active temporary speed restriction on 12841's route: Visakhapatnam -> Rajahmundry (+10 min)
                new Incident(
                        IncidentType.TEMPORARY_RESTRICTION,
                        s.get("VSKP"),
                        s.get("RJY"),
                        IncidentSeverity.MEDIUM,
                        10,
                        "Temporary speed restriction between Visakhapatnam and Rajahmundry may add approximately 10 minutes.",
                        true,
                        now.minusHours(2),
                        now.plusHours(4)
                )
        );

        incidentRepository.saveAll(incidents);
    }
}

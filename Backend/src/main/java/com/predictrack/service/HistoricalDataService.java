package com.predictrack.service;

import com.predictrack.entity.HistoricalDwell;
import com.predictrack.entity.HistoricalSegmentTime;
import com.predictrack.entity.Station;
import com.predictrack.entity.Train;
import com.predictrack.provider.HistoricalDataProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service that calculates expected baseline running times and expected station dwell times
 * using the MEDIAN of historical records (rather than timetable durations or averages that
 * are easily skewed by one-off outliers).
 */
@Service
public class HistoricalDataService {

    private final HistoricalDataProvider historicalDataProvider;

    public HistoricalDataService(HistoricalDataProvider historicalDataProvider) {
        this.historicalDataProvider = historicalDataProvider;
    }

    /**
     * Calculates the median historical running time (in minutes) for a specific train
     * traveling from fromStation to toStation.
     */
    public int getMedianRunningTimeMinutes(Train train, Station fromStation, Station toStation) {
        List<HistoricalSegmentTime> records =
                historicalDataProvider.findSegmentHistory(train, fromStation, toStation);

        if (records == null || records.isEmpty()) {
            throw new InvalidRouteDataException(
                    "Missing historical segment running time data for train " + train.getTrainNumber()
                            + " between " + fromStation.getStationName()
                            + " and " + toStation.getStationName());
        }

        List<Integer> runningTimes = new ArrayList<>(records.size());
        for (HistoricalSegmentTime record : records) {
            runningTimes.add(record.getRunningTimeMinutes());
        }

        return calculateMedian(runningTimes);
    }

    /**
     * Calculates the median historical station dwell time (in minutes) for a specific train
     * at the given station. Returns 0 if no dwell records exist (e.g., at the destination station).
     */
    public int getMedianDwellTimeMinutes(Train train, Station station) {
        List<HistoricalDwell> records = historicalDataProvider.findDwellHistory(train, station);

        if (records == null || records.isEmpty()) {
            return 0;
        }

        List<Integer> dwellTimes = new ArrayList<>(records.size());
        for (HistoricalDwell record : records) {
            dwellTimes.add(record.getDwellMinutes());
        }

        return calculateMedian(dwellTimes);
    }

    /**
     * Computes the median of a list of integer durations:
     * 1. Sort the numbers in ascending order.
     * 2. If the count is odd, return the middle element.
     * 3. If the count is even, return the rounded average of the two middle elements.
     */
    public int calculateMedian(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }

        List<Integer> sorted = new ArrayList<>(values);
        Collections.sort(sorted);

        int n = sorted.size();
        int middle = n / 2;

        if (n % 2 == 1) {
            return sorted.get(middle);
        } else {
            double avg = (sorted.get(middle - 1) + sorted.get(middle)) / 2.0;
            return (int) Math.round(avg);
        }
    }
}

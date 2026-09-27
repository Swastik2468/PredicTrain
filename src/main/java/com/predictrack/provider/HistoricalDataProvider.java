package com.predictrack.provider;

import com.predictrack.entity.HistoricalDwell;
import com.predictrack.entity.HistoricalSegmentTime;
import com.predictrack.entity.Station;
import com.predictrack.entity.Train;

import java.util.List;

/**
 * Abstraction for retrieving historical running times and historical station dwell times.
 */
public interface HistoricalDataProvider {

    List<HistoricalSegmentTime> findSegmentHistory(Train train, Station fromStation, Station toStation);

    List<HistoricalDwell> findDwellHistory(Train train, Station station);
}

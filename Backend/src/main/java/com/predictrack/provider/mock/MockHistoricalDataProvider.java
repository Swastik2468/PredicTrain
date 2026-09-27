package com.predictrack.provider.mock;

import com.predictrack.entity.HistoricalDwell;
import com.predictrack.entity.HistoricalSegmentTime;
import com.predictrack.entity.Station;
import com.predictrack.entity.Train;
import com.predictrack.provider.HistoricalDataProvider;
import com.predictrack.repository.HistoricalDwellRepository;
import com.predictrack.repository.HistoricalSegmentTimeRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mock implementation of HistoricalDataProvider backed by our MySQL database.
 */
@Component
public class MockHistoricalDataProvider implements HistoricalDataProvider {

    private final HistoricalSegmentTimeRepository historicalSegmentTimeRepository;
    private final HistoricalDwellRepository historicalDwellRepository;

    public MockHistoricalDataProvider(
            HistoricalSegmentTimeRepository historicalSegmentTimeRepository,
            HistoricalDwellRepository historicalDwellRepository
    ) {
        this.historicalSegmentTimeRepository = historicalSegmentTimeRepository;
        this.historicalDwellRepository = historicalDwellRepository;
    }

    @Override
    public List<HistoricalSegmentTime> findSegmentHistory(Train train, Station fromStation, Station toStation) {
        return historicalSegmentTimeRepository.findByTrainAndFromStationAndToStation(train, fromStation, toStation);
    }

    @Override
    public List<HistoricalDwell> findDwellHistory(Train train, Station station) {
        return historicalDwellRepository.findByTrainAndStation(train, station);
    }
}

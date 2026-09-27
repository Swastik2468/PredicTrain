package com.predictrack.repository;

import com.predictrack.entity.HistoricalDwell;
import com.predictrack.entity.Station;
import com.predictrack.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalDwellRepository extends JpaRepository<HistoricalDwell, Long> {

    List<HistoricalDwell> findByTrainAndStation(Train train, Station station);

    List<HistoricalDwell> findByTrain_TrainNumberAndStation_StationCode(
            String trainNumber,
            String stationCode
    );
}

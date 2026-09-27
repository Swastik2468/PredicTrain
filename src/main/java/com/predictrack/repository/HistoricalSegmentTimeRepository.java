package com.predictrack.repository;

import com.predictrack.entity.HistoricalSegmentTime;
import com.predictrack.entity.Station;
import com.predictrack.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalSegmentTimeRepository extends JpaRepository<HistoricalSegmentTime, Long> {

    List<HistoricalSegmentTime> findByTrainAndFromStationAndToStation(
            Train train,
            Station fromStation,
            Station toStation
    );

    List<HistoricalSegmentTime> findByTrain_TrainNumberAndFromStation_StationCodeAndToStation_StationCode(
            String trainNumber,
            String fromStationCode,
            String toStationCode
    );
}

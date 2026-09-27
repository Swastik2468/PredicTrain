package com.predictrack.repository;

import com.predictrack.entity.Station;
import com.predictrack.entity.Train;
import com.predictrack.entity.TrainRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainRouteRepository extends JpaRepository<TrainRoute, Long> {

    List<TrainRoute> findByTrainOrderBySequenceNumberAsc(Train train);

    List<TrainRoute> findByTrain_TrainNumberOrderBySequenceNumberAsc(String trainNumber);

    Optional<TrainRoute> findByTrainAndStation(Train train, Station station);
}

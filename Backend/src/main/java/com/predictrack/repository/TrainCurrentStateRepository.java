package com.predictrack.repository;

import com.predictrack.entity.Train;
import com.predictrack.entity.TrainCurrentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainCurrentStateRepository extends JpaRepository<TrainCurrentState, Long> {

    Optional<TrainCurrentState> findByTrain(Train train);

    Optional<TrainCurrentState> findByTrain_TrainNumber(String trainNumber);
}

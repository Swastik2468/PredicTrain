package com.predictrack.provider;

import com.predictrack.entity.Train;
import com.predictrack.entity.TrainCurrentState;
import com.predictrack.entity.TrainRoute;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction for fetching train metadata, ordered route schedules, and live train state.
 * Can be backed by local mock data now and replaced by an official railway running-status API later.
 */
public interface TrainDataProvider {

    Optional<Train> findTrainByNumber(String trainNumber);

    List<TrainRoute> findOrderedRouteByTrain(Train train);

    Optional<TrainCurrentState> findCurrentStateByTrain(Train train);
}

package com.predictrack.provider.mock;

import com.predictrack.entity.Train;
import com.predictrack.entity.TrainCurrentState;
import com.predictrack.entity.TrainRoute;
import com.predictrack.provider.TrainDataProvider;
import com.predictrack.repository.TrainCurrentStateRepository;
import com.predictrack.repository.TrainRepository;
import com.predictrack.repository.TrainRouteRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Mock implementation of TrainDataProvider that reads synthetic railway data
 * from our local MySQL database.
 */
@Component
public class MockTrainDataProvider implements TrainDataProvider {

    private final TrainRepository trainRepository;
    private final TrainRouteRepository trainRouteRepository;
    private final TrainCurrentStateRepository trainCurrentStateRepository;

    public MockTrainDataProvider(
            TrainRepository trainRepository,
            TrainRouteRepository trainRouteRepository,
            TrainCurrentStateRepository trainCurrentStateRepository
    ) {
        this.trainRepository = trainRepository;
        this.trainRouteRepository = trainRouteRepository;
        this.trainCurrentStateRepository = trainCurrentStateRepository;
    }

    @Override
    public Optional<Train> findTrainByNumber(String trainNumber) {
        return trainRepository.findByTrainNumber(trainNumber);
    }

    @Override
    public List<TrainRoute> findOrderedRouteByTrain(Train train) {
        return trainRouteRepository.findByTrainOrderBySequenceNumberAsc(train);
    }

    @Override
    public Optional<TrainCurrentState> findCurrentStateByTrain(Train train) {
        return trainCurrentStateRepository.findByTrain(train);
    }
}

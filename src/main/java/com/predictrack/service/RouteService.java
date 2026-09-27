package com.predictrack.service;

import com.predictrack.entity.Station;
import com.predictrack.entity.Train;
import com.predictrack.entity.TrainRoute;
import com.predictrack.provider.TrainDataProvider;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for retrieving, validating, and slicing a train's ordered station route.
 */
@Service
public class RouteService {

    private final TrainDataProvider trainDataProvider;

    public RouteService(TrainDataProvider trainDataProvider) {
        this.trainDataProvider = trainDataProvider;
    }

    /**
     * Fetches the complete ordered route for a train and validates its sequence integrity.
     */
    public List<TrainRoute> getOrderedRoute(Train train) {
        List<TrainRoute> route = trainDataProvider.findOrderedRouteByTrain(train);
        validateRouteSequence(train, route);
        return route;
    }

    /**
     * Returns the portion of the route starting at currentStation up to the final destination.
     */
    public List<TrainRoute> getRemainingRouteFromCurrentStation(
            List<TrainRoute> orderedRoute,
            Station currentStation,
            Station nextStation
    ) {
        if (currentStation == null || currentStation.getStationCode() == null) {
            throw new InvalidRouteDataException("Current station is missing or invalid.");
        }

        int currentIndex = -1;
        for (int i = 0; i < orderedRoute.size(); i++) {
            Station routeStation = orderedRoute.get(i).getStation();
            if (routeStation != null
                    && routeStation.getStationCode() != null
                    && routeStation.getStationCode().equalsIgnoreCase(currentStation.getStationCode())) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            throw new InvalidRouteDataException(
                    "Current station '" + currentStation.getStationName()
                            + "' does not belong to the train's route.");
        }

        // If the train has not yet reached the final station, verify nextStation matches currentIndex + 1
        if (currentIndex < orderedRoute.size() - 1) {
            if (nextStation == null || nextStation.getStationCode() == null) {
                throw new InvalidRouteDataException(
                        "Next station is missing for a train that has not yet reached its final destination.");
            }
            Station expectedNext = orderedRoute.get(currentIndex + 1).getStation();
            if (!expectedNext.getStationCode().equalsIgnoreCase(nextStation.getStationCode())) {
                throw new InvalidRouteDataException(
                        "Invalid route state: expected next station after " + currentStation.getStationName()
                                + " to be " + expectedNext.getStationName()
                                + ", but found " + nextStation.getStationName());
            }
        }

        return orderedRoute.subList(currentIndex, orderedRoute.size());
    }

    /**
     * Validates that a train's route has at least 2 stations and strictly increasing sequence numbers.
     */
    public void validateRouteSequence(Train train, List<TrainRoute> route) {
        if (route == null || route.size() < 2) {
            throw new InvalidRouteDataException(
                    "Invalid or missing route data for train " + train.getTrainNumber()
                            + ": route must contain at least 2 stations.");
        }

        int previousSequence = 0;
        for (TrainRoute stop : route) {
            if (stop == null || stop.getStation() == null) {
                throw new InvalidRouteDataException(
                        "Route entry or station is null for train " + train.getTrainNumber());
            }
            if (stop.getSequenceNumber() == null || stop.getSequenceNumber() <= previousSequence) {
                throw new InvalidRouteDataException(
                        "Invalid route sequence number for train " + train.getTrainNumber()
                                + " at station " + stop.getStation().getStationName());
            }
            previousSequence = stop.getSequenceNumber();
        }
    }
}

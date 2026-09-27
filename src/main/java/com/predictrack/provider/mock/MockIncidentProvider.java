package com.predictrack.provider.mock;

import com.predictrack.entity.Incident;
import com.predictrack.entity.Station;
import com.predictrack.provider.IncidentProvider;
import com.predictrack.repository.IncidentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mock implementation of IncidentProvider backed by our MySQL database.
 */
@Component
public class MockIncidentProvider implements IncidentProvider {

    private final IncidentRepository incidentRepository;

    public MockIncidentProvider(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Override
    public List<Incident> findActiveIncidentsForSegment(Station fromStation, Station toStation) {
        return incidentRepository.findByFromStationAndToStationAndActiveTrue(fromStation, toStation);
    }
}

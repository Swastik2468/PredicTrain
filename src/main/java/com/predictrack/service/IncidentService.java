package com.predictrack.service;

import com.predictrack.entity.Incident;
import com.predictrack.entity.IncidentType;
import com.predictrack.entity.Station;
import com.predictrack.provider.IncidentProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that evaluates active operational incidents along railway segments
 * and calculates incident-related delays and human-readable explanations.
 */
@Service
public class IncidentService {

    private final IncidentProvider incidentProvider;

    public IncidentService(IncidentProvider incidentProvider) {
        this.incidentProvider = incidentProvider;
    }

    /**
     * Holds the total active incident delay (in minutes) and human-readable explanations
     * for a single railway segment (fromStation -> toStation).
     */
    public record SegmentIncidentImpact(int delayMinutes, List<String> explanations) {
    }

    /**
     * Finds all active incidents on a segment (fromStation -> toStation), sums their
     * estimated delay minutes, and builds human-readable explanations.
     * Inactive incidents (active = false) are ignored.
     */
    public SegmentIncidentImpact evaluateSegmentIncidents(Station fromStation, Station toStation) {
        List<Incident> incidents = incidentProvider.findActiveIncidentsForSegment(fromStation, toStation);
        if (incidents == null || incidents.isEmpty()) {
            return new SegmentIncidentImpact(0, List.of());
        }

        int totalDelay = 0;
        List<String> explanations = new ArrayList<>();

        for (Incident incident : incidents) {
            // Defensive check: ensure only active incidents with positive delay are counted
            if (Boolean.TRUE.equals(incident.getActive())
                    && incident.getEstimatedDelayMinutes() != null
                    && incident.getEstimatedDelayMinutes() > 0) {
                int delay = incident.getEstimatedDelayMinutes();
                totalDelay += delay;
                explanations.add(buildIncidentExplanation(incident, fromStation, toStation, delay));
            }
        }

        return new SegmentIncidentImpact(totalDelay, explanations);
    }

    private String buildIncidentExplanation(
            Incident incident,
            Station fromStation,
            Station toStation,
            int delayMinutes
    ) {
        if (incident.getDescription() != null && !incident.getDescription().isBlank()) {
            return incident.getDescription();
        }

        String label = formatIncidentType(incident.getType());
        return label + " between " + fromStation.getStationName()
                + " and " + toStation.getStationName()
                + " may add approximately " + delayMinutes + " minutes.";
    }

    private String formatIncidentType(IncidentType type) {
        if (type == null) {
            return "Operational disruption";
        }
        return switch (type) {
            case CONGESTION -> "Congestion";
            case TRACK_FAULT -> "Track fault";
            case ACCIDENT -> "Operational incident";
            case SIGNAL_FAILURE -> "Signal failure";
            case TEMPORARY_RESTRICTION -> "Temporary speed restriction";
        };
    }
}

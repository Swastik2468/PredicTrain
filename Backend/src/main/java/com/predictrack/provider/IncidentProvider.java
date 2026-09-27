package com.predictrack.provider;

import com.predictrack.entity.Incident;
import com.predictrack.entity.Station;

import java.util.List;

/**
 * Abstraction for fetching active operational incidents along a railway segment.
 * Can later be replaced by an official railway control-office incident feed.
 */
public interface IncidentProvider {

    List<Incident> findActiveIncidentsForSegment(Station fromStation, Station toStation);
}

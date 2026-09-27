package com.predictrack.repository;

import com.predictrack.entity.Incident;
import com.predictrack.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByActiveTrue();

    List<Incident> findByFromStationAndToStationAndActiveTrue(
            Station fromStation,
            Station toStation
    );

    List<Incident> findByFromStation_StationCodeAndToStation_StationCodeAndActiveTrue(
            String fromStationCode,
            String toStationCode
    );
}

package com.predictrack.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a coaching train in PredicTrack.
 */
@Entity
@Table(name = "trains")
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String trainNumber;

    @Column(nullable = false, length = 120)
    private String trainName;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false, length = 100)
    private String destination;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNumber ASC")
    private List<TrainRoute> routes = new ArrayList<>();

    @OneToOne(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private TrainCurrentState currentState;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricalSegmentTime> historicalSegmentTimes = new ArrayList<>();

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricalDwell> historicalDwells = new ArrayList<>();

    public Train() {
    }

    public Train(String trainNumber, String trainName, String source, String destination) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<TrainRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<TrainRoute> routes) {
        this.routes = routes;
    }

    public TrainCurrentState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(TrainCurrentState currentState) {
        this.currentState = currentState;
    }

    public List<HistoricalSegmentTime> getHistoricalSegmentTimes() {
        return historicalSegmentTimes;
    }

    public void setHistoricalSegmentTimes(List<HistoricalSegmentTime> historicalSegmentTimes) {
        this.historicalSegmentTimes = historicalSegmentTimes;
    }

    public List<HistoricalDwell> getHistoricalDwells() {
        return historicalDwells;
    }

    public void setHistoricalDwells(List<HistoricalDwell> historicalDwells) {
        this.historicalDwells = historicalDwells;
    }
}

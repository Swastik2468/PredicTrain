package com.predictrack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Represents the live operational state of a train along its route.
 * Uses currentStation, nextStation, and progressPercentage (0-100)
 * instead of requiring live GPS coordinates in the initial prototype.
 */
@Entity
@Table(name = "train_current_states")
public class TrainCurrentState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "train_id", nullable = false, unique = true)
    private Train train;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "current_station_id", nullable = false)
    private Station currentStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "next_station_id")
    private Station nextStation;

    @Column(nullable = false)
    private Integer progressPercentage;

    @Column(nullable = false)
    private Integer currentDelayMinutes;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    public TrainCurrentState() {
    }

    public TrainCurrentState(
            Train train,
            Station currentStation,
            Station nextStation,
            Integer progressPercentage,
            Integer currentDelayMinutes,
            LocalDateTime lastUpdated
    ) {
        this.train = train;
        this.currentStation = currentStation;
        this.nextStation = nextStation;
        this.progressPercentage = progressPercentage;
        this.currentDelayMinutes = currentDelayMinutes;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public Station getCurrentStation() {
        return currentStation;
    }

    public void setCurrentStation(Station currentStation) {
        this.currentStation = currentStation;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public void setNextStation(Station nextStation) {
        this.nextStation = nextStation;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public Integer getCurrentDelayMinutes() {
        return currentDelayMinutes;
    }

    public void setCurrentDelayMinutes(Integer currentDelayMinutes) {
        this.currentDelayMinutes = currentDelayMinutes;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

package com.predictrack.service;

/**
 * Thrown when a requested train number does not exist in the system (maps to HTTP 404).
 */
public class TrainNotFoundException extends RuntimeException {

    public TrainNotFoundException(String message) {
        super(message);
    }
}

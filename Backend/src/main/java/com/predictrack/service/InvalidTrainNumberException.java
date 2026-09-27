package com.predictrack.service;

/**
 * Thrown when a requested train number has an invalid format (maps to HTTP 400).
 */
public class InvalidTrainNumberException extends RuntimeException {

    public InvalidTrainNumberException(String message) {
        super(message);
    }
}

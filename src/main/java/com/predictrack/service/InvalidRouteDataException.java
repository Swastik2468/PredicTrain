package com.predictrack.service;

/**
 * Thrown when route sequence data, current train state, or historical segment data
 * is missing or invalid (prevents raw NullPointerExceptions and returns a meaningful error).
 */
public class InvalidRouteDataException extends RuntimeException {

    public InvalidRouteDataException(String message) {
        super(message);
    }
}

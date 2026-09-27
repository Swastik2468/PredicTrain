package com.predictrack.controller;

import com.predictrack.dto.ErrorResponse;
import com.predictrack.service.InvalidRouteDataException;
import com.predictrack.service.InvalidTrainNumberException;
import com.predictrack.service.TrainNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralized global exception handler that translates domain/validation exceptions
 * into clean, consistent HTTP JSON responses:
 * - TrainNotFoundException      -> HTTP 404 Not Found
 * - InvalidTrainNumberException -> HTTP 400 Bad Request
 * - InvalidRouteDataException   -> HTTP 400 Bad Request
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(TrainNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTrainNotFound(TrainNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidTrainNumberException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTrainNumber(InvalidTrainNumberException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidRouteDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRouteData(InvalidRouteDataException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + ex.getMessage()
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now().format(TIMESTAMP_FORMATTER)
        );
        return ResponseEntity.status(status).body(body);
    }
}

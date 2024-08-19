package com.apipietunes.clients.service.exception;

public class TrackAlreadyExistsException extends RuntimeException {
    public TrackAlreadyExistsException(String message) {
        super(message);
    }
}

package com.apipietunes.clients.services.exceptions;

public class TrackAlreadyExistsException extends RuntimeException {
    public TrackAlreadyExistsException(String message) {
        super(message);
    }
}

package com.apipietunes.clients.service.exception;

public class NodeAlreadyExists extends RuntimeException {
    public NodeAlreadyExists(String message) {
        super(message);
    }
}

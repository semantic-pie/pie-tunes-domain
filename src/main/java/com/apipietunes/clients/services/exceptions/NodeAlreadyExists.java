package com.apipietunes.clients.services.exceptions;

public class NodeAlreadyExists extends RuntimeException {
    public NodeAlreadyExists(String message) {
        super(message);
    }
}

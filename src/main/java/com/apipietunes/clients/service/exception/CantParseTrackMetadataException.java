package com.apipietunes.clients.service.exception;

public class CantParseTrackMetadataException extends RuntimeException {
    
    public CantParseTrackMetadataException(String msg) {
        super(msg);
    }
}

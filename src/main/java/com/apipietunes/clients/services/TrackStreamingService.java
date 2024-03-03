package com.apipietunes.clients.services;

import java.io.InputStream;

import reactor.core.publisher.Mono;

public interface TrackStreamingService {
    Mono<InputStream> getById(String id);
}

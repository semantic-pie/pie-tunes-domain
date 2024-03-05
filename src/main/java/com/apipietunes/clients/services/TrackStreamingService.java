package com.apipietunes.clients.services;

import io.minio.GetObjectResponse;
import reactor.core.publisher.Mono;

public interface TrackStreamingService {
    Mono<GetObjectResponse> getTrackById(String id);

    Mono<GetObjectResponse> getTrackCoverById(String id);
}

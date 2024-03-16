package com.apipietunes.clients.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apipietunes.clients.services.TrackStreamingService;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackStreamingServiceImpl implements TrackStreamingService {

    @Value("${minio.buckets.tracks}")
    public String TRACKS_BUCKET;

    @Value("${minio.buckets.covers}")
    public String COVERS_BUCKET;

    private final MinioClient minioClient;

    @Override
    public Mono<GetObjectResponse> getTrackById(String id) {
        try {
            return Mono.just(minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(TRACKS_BUCKET)
                            .object(id)
                            .build()));
        } catch (Exception ex) {
            return Mono.empty();
        }
    }

    @Override
    public Mono<GetObjectResponse> getTrackCoverById(String id) {
        try {
            return Mono.just(minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(COVERS_BUCKET)
                            .object(id)
                            .build()));
        } catch (Exception ex) {
            return Mono.empty();
        }

    }
}
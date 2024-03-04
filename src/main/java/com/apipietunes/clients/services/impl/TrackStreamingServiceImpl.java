package com.apipietunes.clients.services.impl;

import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apipietunes.clients.services.TrackStreamingService;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackStreamingServiceImpl implements TrackStreamingService {

    @Value("${minio.bucket}")
    public String BUCKET_NAME;

    private final MinioClient minioClient;

    @Override
    public Mono<InputStream> getById(String id) {
        try {
            return Mono.just(minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(id)
                            .build()));
        } catch (Exception ex) {
            return Mono.empty();
        }
    }
}
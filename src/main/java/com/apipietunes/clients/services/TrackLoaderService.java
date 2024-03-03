package com.apipietunes.clients.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.http.codec.multipart.FilePart;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;

import io.minio.GetObjectResponse;
import reactor.core.publisher.Mono;

public interface TrackLoaderService {
    Mono<MusicTrack> save(FilePart filePart);

    Mono<Void> saveAll(List<FilePart> fileParts);

    Mono<GetObjectResponse> load(String id);
}

package com.apipietunes.clients.services;

import org.springframework.http.codec.multipart.FilePart;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;

import reactor.core.publisher.Mono;

public interface TrackLoaderService {
 // void save(Flux<MultipartFile> tracks);
 Mono<MusicTrack> save(FilePart filePart);
//  MusicTrack save(FilePart filePart);
}

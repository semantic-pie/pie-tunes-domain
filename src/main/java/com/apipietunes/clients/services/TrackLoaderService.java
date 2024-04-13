package com.apipietunes.clients.services;

import java.util.List;

import org.springframework.http.codec.multipart.FilePart;

import com.apipietunes.clients.models.MusicTrack;

import reactor.core.publisher.Mono;

public interface TrackLoaderService {
    Mono<MusicTrack> save(FilePart filePart);

    Mono<Void> saveAll(List<FilePart> fileParts);
}

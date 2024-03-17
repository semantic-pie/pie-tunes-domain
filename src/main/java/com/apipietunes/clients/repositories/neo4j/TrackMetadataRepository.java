package com.apipietunes.clients.repositories.neo4j;

import java.util.UUID;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;


import reactor.core.publisher.Mono;


public interface TrackMetadataRepository extends ReactiveNeo4jRepository<MusicTrack, UUID> {
    Mono<MusicTrack> findByTitleAndMusicBand_Name(String title, String musicBand_Name);

    Mono<MusicTrack> findByUuid(UUID uuid);

}

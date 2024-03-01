package com.apipietunes.clients.repositories;

import java.util.UUID;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.neo4jDomain.MusicGenre;

import reactor.core.publisher.Mono;



public interface MusicGenreRepository extends ReactiveNeo4jRepository<MusicGenre, UUID> {
    Mono<MusicGenre> findByName(String name);
}

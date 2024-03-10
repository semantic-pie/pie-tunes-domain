package com.apipietunes.clients.repositories.neo4j;

import java.util.UUID;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.apipietunes.clients.models.neo4jDomain.MusicBand;

import reactor.core.publisher.Mono;



public interface MusicBandRepository extends ReactiveNeo4jRepository<MusicBand, UUID> {
    Mono<MusicBand> findByName(String name);
    Mono<Boolean> existsByName(String name);
}

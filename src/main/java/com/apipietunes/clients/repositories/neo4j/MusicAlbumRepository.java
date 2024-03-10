package com.apipietunes.clients.repositories.neo4j;

import java.util.UUID;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;


import reactor.core.publisher.Mono;



public interface MusicAlbumRepository extends ReactiveNeo4jRepository<MusicAlbum, UUID> {
    Mono<MusicAlbum> findByName(String name);
}

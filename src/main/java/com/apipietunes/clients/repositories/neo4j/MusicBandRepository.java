package com.apipietunes.clients.repositories.neo4j;

import java.util.UUID;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.apipietunes.clients.models.neo4jDomain.MusicBand;

import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;


public interface MusicBandRepository extends ReactiveNeo4jRepository<MusicBand, UUID> {

    Mono<MusicBand> findMusicBandByName(String name);

    @Query("""
           MERGE (b:Band {name: :#{#musicBand.name}})
           ON CREATE SET b.uuid = toString(randomUUID()), b.version = 0
           RETURN b
           """)
    Mono<MusicBand> persist(@Param("musicBand") MusicBand musicBand);

}

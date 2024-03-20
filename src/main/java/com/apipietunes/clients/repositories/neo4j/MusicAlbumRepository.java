package com.apipietunes.clients.repositories.neo4j;

import java.util.UUID;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;


import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;


public interface MusicAlbumRepository extends ReactiveNeo4jRepository<MusicAlbum, UUID> {
    @Query("""
            MERGE (a:Album {name: :#{#musicAlbum.name}})
            ON CREATE SET a.uuid = toString(randomUUID()),  a.version = 0
            WITH a
            MERGE (b:Band {name: :#{#musicAlbum.musicBand.name}})
            ON CREATE SET b.uuid = toString(randomUUID()),  b.version = 0
            MERGE (b) - [:HAS_ALBUM] -> (a)
            RETURN a
            """)
    Mono<MusicAlbum> persist(@Param("musicAlbum") MusicAlbum musicAlbum);

    Mono<MusicAlbum> findMusicAlbumByName(String name);
}

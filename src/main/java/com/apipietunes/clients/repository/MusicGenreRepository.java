package com.apipietunes.clients.repository;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.apipietunes.clients.model.entity.MusicGenre;

import reactor.core.publisher.Mono;

@Repository
public interface MusicGenreRepository extends ReactiveNeo4jRepository<MusicGenre, String> {
    @Query("""
            MERGE (g:Genre {name: :#{#musicGenre.name}})
            ON CREATE SET g.version = 0
            RETURN g
            """)
    Mono<MusicGenre> persist(@Param("musicGenre") MusicGenre musicGenre);

    Mono<MusicGenre> findMusicGenreByName(String name);
}

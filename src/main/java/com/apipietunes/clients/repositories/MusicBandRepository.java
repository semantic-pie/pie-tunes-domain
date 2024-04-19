package com.apipietunes.clients.repositories;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.apipietunes.clients.models.MusicBand;

import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface MusicBandRepository extends ReactiveNeo4jRepository<MusicBand, UUID> {

    Mono<MusicBand> findMusicBandByName(String name);

    @Query("""
           MERGE (b:Band {name: :#{#musicBand.name}})
           ON CREATE SET b.uuid = toString(randomUUID()), b.version = 0
           RETURN b
           """)
    Mono<MusicBand> persist(@Param("musicBand") MusicBand musicBand);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(b:Band)
            WITH COUNT(b) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedBands(String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicBand:Band)
            RETURN musicBand
            :#{orderBy(#pageable)}
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicBand> findAllLikedBands(String userUuid, Pageable pageable);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(b:Band)
            WHERE toLower(b.name) CONTAINS toLower($searchQuery)
            WITH COUNT(b) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedBandsByTitle(String searchQuery, String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicBand:Band)
            WHERE toLower(musicBand.name) CONTAINS toLower($searchQuery)
            RETURN musicBand
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicBand> findAllLikedBandsByTitle(String searchQuery, String userUuid, Pageable pageable);

    @Query("""
            MATCH (musicBand:Band) RETURN musicBand
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicBand> findAllBands(Pageable pageable);

    @Query("""
            MATCH (musicBand:Band {uuid: $bandUuid})
            MATCH (musicBand)-[has_album:HAS_ALBUM]->(musicBand_albums:Album)
            RETURN musicBand, collect(has_album), collect(musicBand_albums)
            """)
    Mono<MusicBand> findMusicBandByUuid(String bandUuid);
}

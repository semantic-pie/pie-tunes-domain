package com.apipietunes.clients.repositories;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import com.apipietunes.clients.models.MusicAlbum;

import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
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

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(a:Album)
            WITH COUNT(a) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedAlbums(String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicAlbum:Album)
            MATCH (musicAlbum)<-[has_album:HAS_ALBUM]-(musicAlbum_musicBand:Band)
            WITH musicAlbum, collect(has_album) AS has_album, collect(musicAlbum_musicBand) AS musicAlbum_musicBand, r 
            :#{orderBy(#pageable)}
            RETURN musicAlbum, has_album, musicAlbum_musicBand
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicAlbum> findAllLikedAlbums(String userUuid, Pageable pageable);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(a:Album)
            WHERE toLower(a.name) CONTAINS toLower($searchQuery)
            WITH COUNT(a) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedAlbumsByTitle(String searchQuery, String userUuid);

    @Query("""   
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicAlbum:Album)
            WHERE toLower(musicAlbum.name) CONTAINS toLower($searchQuery)
            MATCH (musicAlbum)<-[has_album:HAS_ALBUM]-(musicAlbum_musicBand:Band)
            return musicAlbum, collect(has_album), collect(musicAlbum_musicBand)
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicAlbum> findAllLikedAlbumsByTitle(String searchQuery, String userUuid, Pageable pageable);

    @Query("""
            MATCH (musicAlbum:Album)<-[has_album:HAS_ALBUM]-(musicBand:Band)
            RETURN musicAlbum, collect(has_album), collect(musicBand)
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicAlbum> findAllAlbums(Pageable pageable);

    @Query("""
            MATCH (musicBand:Band)-[has_album:HAS_ALBUM]->(musicAlbum:Album {uuid: $albumUuid})
            MATCH (musicAlbum)-[contains:CONTAINS]->(musicAlbum_tracks:Track)
            return musicAlbum, collect(has_album), collect(musicBand), collect(contains), collect(musicAlbum_tracks)
            """)
    Mono<MusicAlbum> findMusicAlbumByUuid(String albumUuid);
}

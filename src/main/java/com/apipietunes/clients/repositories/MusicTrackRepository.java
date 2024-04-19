package com.apipietunes.clients.repositories;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import com.apipietunes.clients.models.MusicTrack;


import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface MusicTrackRepository extends ReactiveNeo4jRepository<MusicTrack, UUID> {
    Mono<MusicTrack> findByTitleAndMusicBand_Name(String title, String musicBand_Name);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(t:Track)
            WITH COUNT(t) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedTracks(String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicTrack:Track)<-[a:HAS_TRACK]-(band:Band)
            MATCH (musicTrack)<-[contains:CONTAINS]-(album:Album)
            WITH musicTrack,
            collect(a) as trackInfo,
            collect(contains) as containsOfAlbum,
            collect(band) as bands,
            collect(album) as albums, r
            ORDER BY r.createdAt DESC
            RETURN musicTrack, trackInfo, bands, albums, containsOfAlbum
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicTrack> findAllLikedTracks(String userUuid, Pageable pageable);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(t:Track)
            WHERE toLower(t.title) CONTAINS toLower($searchQuery)
            WITH COUNT(t) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedTracksByTitle(String searchQuery, String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicTrack:Track)<-[has_track:HAS_TRACK]-(band:Band)
            WHERE toLower(musicTrack.title) CONTAINS toLower($searchQuery)
            MATCH (musicTrack)-[in_genre:IN_GENRE]->(genre:Genre)
            MATCH (musicTrack)<-[contains:CONTAINS]-(album:Album)
            RETURN musicTrack, collect(has_track), collect(band), collect(in_genre), collect(genre), collect(contains), collect(album)
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicTrack> findAllLikedTracksByTitle(String searchQuery, String userUuid, Pageable pageable);

    @Query("""
            MATCH (a:Album {uuid: $albumUuid})-[r:CONTAINS]->(t:Track)
            WITH COUNT(t) AS total
            RETURN total
            """)
    Mono<Long> findTotalTracksInAlbumByUuid(String albumUuid);

    @Query("""
            MATCH (album:Album {uuid: $albumUuid})-[r:CONTAINS]->(musicTrack:Track)<-[has_track:HAS_TRACK]-(band:Band)
            MATCH (musicTrack)-[in_genre:IN_GENRE]->(genre:Genre)
            RETURN musicTrack, collect(r), collect(album), collect(has_track), collect(band), collect(in_genre), collect(genre)
            """)
    Flux<MusicTrack> findTracksByAlbumUuid(String albumUuid);

    @Query("""
            MATCH (album:Album)-[contains:CONTAINS]->(musicTrack:Track {uuid: $trackUuid})<-[has_track:HAS_TRACK]-(band:Band)
            RETURN musicTrack, collect(contains), collect(album), collect(has_track), collect(band)
            """)
    Mono<MusicTrack> findMusicTrackByUuid(String trackUuid);


    @Query("""
            MATCH (album:Album)-[contains:CONTAINS]->(musicTrack:Track)<-[has_track:HAS_TRACK]-(band:Band)
            RETURN musicTrack, collect(contains), collect(album), collect(has_track), collect(band)
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicTrack> findAllTracks(Pageable pageable);

}

package com.apipietunes.clients.repositories.neo4j;

import java.util.UUID;

import com.apipietunes.clients.models.dtos.SearchEntityResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;


import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface TrackMetadataRepository extends ReactiveNeo4jRepository<MusicTrack, UUID> {
    Mono<MusicTrack> findByTitleAndMusicBand_Name(String title, String musicBand_Name);

    Mono<MusicTrack> findByUuid(UUID uuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(t:Track)
            WITH COUNT(t) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedTracks(String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicTrack:Track)
            RETURN musicTrack{
             .bitrate,
             .lengthInMilliseconds,
             .releaseYear,
             .title,
             .uuid,
             .version,
             __nodeLabels__: labels(musicTrack),
             __elementId__: id(musicTrack),
             Track_CONTAINS_Album: [(musicTrack)<-[:CONTAINS]-(musicTrack_musicAlbum:Album) | musicTrack_musicAlbum{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 .yearOfRecord,
                 __nodeLabels__: labels(musicTrack_musicAlbum),
                 __elementId__: id(musicTrack_musicAlbum)
             }],
             Track_IN_GENRE_Genre: [(musicTrack)-[:IN_GENRE]->(musicTrack_genres:Genre) | musicTrack_genres{
                 .name,
                 .version,
                 __nodeLabels__: labels(musicTrack_genres),
                 __elementId__: id(musicTrack_genres)
             }],
             Track_HAS_TRACK_Band: [(musicTrack)<-[:HAS_TRACK]-(musicTrack_musicBand:Band) | musicTrack_musicBand{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 __nodeLabels__: labels(musicTrack_musicBand),
                 __elementId__: id(musicTrack_musicBand)
             }]
            }
            :#{orderBy(#pageable)}
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicTrack> findAllLikedTracks(String userUuid, Pageable pageable);

}

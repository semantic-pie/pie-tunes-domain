package com.apipietunes.clients.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.apipietunes.clients.model.entity.MusicTrack;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MusicTrackRepository extends ReactiveNeo4jRepository<MusicTrack, UUID> {
    Mono<MusicTrack> findByTitleAndMusicBand_Name(String title, String musicBand_Name);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(t:Track)
            WITH COUNT(t) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedTracks(String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicTrack:Track)<-[:HAS_TRACK]-(band:Band)
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
                 __elementId__: id(musicTrack_musicAlbum),
                 Album_HAS_ALBUM_Band: [band{
                             .description,
                             .name,
                             .uuid,
                             .version,
                             __nodeLabels__: labels(band),
                             __elementId__: id(band)
                 }]
             }],
             Track_IN_GENRE_Genre: [(musicTrack)-[:IN_GENRE]->(musicTrack_genres:Genre) | musicTrack_genres{
                 .name,
                 .version,
                 __nodeLabels__: labels(musicTrack_genres),
                 __elementId__: id(musicTrack_genres)
             }],
             Track_HAS_TRACK_Band: [band{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 __nodeLabels__: labels(band),
                 __elementId__: id(band)
             }]
            }
            :#{orderBy(#pageable)}
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
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicTrack:Track)<-[:HAS_TRACK]-(band:Band)
            WHERE toLower(musicTrack.title) CONTAINS toLower($searchQuery)
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
                 __elementId__: id(musicTrack_musicAlbum),
                 Album_HAS_ALBUM_Band: [band{
                             .description,
                             .name,
                             .uuid,
                             .version,
                             __nodeLabels__: labels(band),
                             __elementId__: id(band)
                 }]
             }],
             Track_IN_GENRE_Genre: [(musicTrack)-[:IN_GENRE]->(musicTrack_genres:Genre) | musicTrack_genres{
                 .name,
                 .version,
                 __nodeLabels__: labels(musicTrack_genres),
                 __elementId__: id(musicTrack_genres)
             }],
             Track_HAS_TRACK_Band: [band{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 __nodeLabels__: labels(band),
                 __elementId__: id(band)
             }]
            }
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
            MATCH (album:Album {uuid: $albumUuid})-[r:CONTAINS]->(musicTrack:Track)<-[:HAS_TRACK]-(band:Band)
            RETURN musicTrack{
             .bitrate,
             .lengthInMilliseconds,
             .releaseYear,
             .title,
             .uuid,
             .version,
             __nodeLabels__: labels(musicTrack),
             __elementId__: id(musicTrack),
             Track_CONTAINS_Album: [album{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 .yearOfRecord,
                 __nodeLabels__: labels(album),
                 __elementId__: id(album)
             }],
             Track_IN_GENRE_Genre: [(musicTrack)-[:IN_GENRE]->(musicTrack_genres:Genre) | musicTrack_genres{
                 .name,
                 .version,
                 __nodeLabels__: labels(musicTrack_genres),
                 __elementId__: id(musicTrack_genres)
             }],
             Track_HAS_TRACK_Band: [band{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 __nodeLabels__: labels(band),
                 __elementId__: id(band)
             }]
            }
            """)
    Flux<MusicTrack> findTracksByAlbumUuid(String albumUuid);

    @Query("""
            MATCH (album:Album)-[:CONTAINS]->(musicTrack:Track {uuid: $trackUuid})<-[:HAS_TRACK]-(band:Band)
            RETURN musicTrack{
             .bitrate,
             .lengthInMilliseconds,
             .releaseYear,
             .title,
             .uuid,
             .version,
             __nodeLabels__: labels(musicTrack),
             __elementId__: id(musicTrack),
             Track_CONTAINS_Album: [album{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 .yearOfRecord,
                 __nodeLabels__: labels(album),
                 __elementId__: id(album)
             }],
             Track_HAS_TRACK_Band: [band{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 __nodeLabels__: labels(band),
                 __elementId__: id(band)
             }]
             }
            """)
    Mono<MusicTrack> findMusicTrackByUuid(String trackUuid);


    @Query("""
            MATCH (album:Album)-[:CONTAINS]->(musicTrack:Track)<-[:HAS_TRACK]-(band:Band)
            RETURN musicTrack{
             .bitrate,
             .lengthInMilliseconds,
             .releaseYear,
             .title,
             .uuid,
             .version,
             __nodeLabels__: labels(musicTrack),
             __elementId__: id(musicTrack),
             Track_CONTAINS_Album: [album{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 .yearOfRecord,
                 __nodeLabels__: labels(album),
                 __elementId__: id(album)
             }],
             Track_HAS_TRACK_Band: [band{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 __nodeLabels__: labels(band),
                 __elementId__: id(band)
             }]
             }
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicTrack> findAllTracks(Pageable pageable);

}

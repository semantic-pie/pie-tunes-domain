package com.apipietunes.clients.repositories.neo4j;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.neo4jDomain.MusicTrack;

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
            RETURN musicAlbum{
                .description,
                .name,
                .uuid,
                .version,
                .yearOfRecord,
                __nodeLabels__: labels(musicAlbum),
                __elementId__: id(musicAlbum),
                Album_HAS_ALBUM_Band: [(musicAlbum)<-[:HAS_ALBUM]-(musicAlbum_musicBand:Band) | musicAlbum_musicBand{
                    .description,
                    .name,
                    .uuid,
                    .version,
                    __nodeLabels__: labels(musicAlbum_musicBand),
                    __elementId__: id(musicAlbum_musicBand)
                }]
            }
            :#{orderBy(#pageable)}
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
            RETURN musicAlbum{
                .description,
                .name,
                .uuid,
                .version,
                .yearOfRecord,
                __nodeLabels__: labels(musicAlbum),
                __elementId__: id(musicAlbum),
                Album_HAS_ALBUM_Band: [(musicAlbum)<-[:HAS_ALBUM]-(musicAlbum_musicBand:Band) | musicAlbum_musicBand{
                    .description,
                    .name,
                    .uuid,
                    .version,
                    __nodeLabels__: labels(musicAlbum_musicBand),
                    __elementId__: id(musicAlbum_musicBand)
                }]
            }
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicAlbum> findAllLikedAlbumsByTitle(String searchQuery, String userUuid, Pageable pageable);

    @Query("""
            MATCH (musicAlbum:Album)<-[:HAS_ALBUM]-(musicBand:Band)
            RETURN musicAlbum{
             .yearOfRecord,
             .name,
             .uuid,
             .description,
             .version,
             __nodeLabels__: labels(musicAlbum),
             __elementId__: id(musicAlbum),
             Album_HAS_ALBUM_Band: [musicBand{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 __nodeLabels__: labels(musicBand),
                 __elementId__: id(musicBand)
             }]
             }
            """)
    Flux<MusicAlbum> findAllAlbums();

    @Query("""
            MATCH (musicBand:Band)-[:HAS_ALBUM]->(musicAlbum:Album {uuid: $albumUuid})
            RETURN musicAlbum{
             .yearOfRecord,
             .name,
             .uuid,
             .description,
             .version,
             __nodeLabels__: labels(musicAlbum),
             __elementId__: id(musicAlbum),
             Album_CONTAINS_Track: [(musicAlbum)-[:CONTAINS]->(musicAlbum_tracks:Track) | musicAlbum_tracks{
                .bitrate,
                .lengthInMilliseconds,
                .releaseYear,
                .title,
                .uuid,
                .version,
                __nodeLabels__: labels(musicAlbum_tracks),
                __elementId__: id(musicAlbum_tracks)
             }],
             Album_HAS_ALBUM_Band: [musicBand{
                 .description,
                 .name,
                 .uuid,
                 .version,
                 __nodeLabels__: labels(musicBand),
                 __elementId__: id(musicBand)
             }]
             }
            """)
    Mono<MusicAlbum> findMusicAlbumByUuid(String albumUuid);
}

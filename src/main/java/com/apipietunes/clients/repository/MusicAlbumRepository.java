package com.apipietunes.clients.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.apipietunes.clients.model.entity.MusicAlbum;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MusicAlbumRepository extends ReactiveNeo4jRepository<MusicAlbum, UUID> {

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
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<MusicAlbum> findAllAlbums(Pageable pageable);

    Mono<MusicAlbum> findMusicAlbumByUuid(UUID uuid);

    Flux<MusicAlbum> findAllByNameContainingIgnoreCase(String searchQuery);
}

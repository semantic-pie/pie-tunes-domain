package com.apipietunes.clients.repository.globalsearch;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.apipietunes.clients.model.dto.domain.MusicTrackDto;

import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface TrackSearchRepository extends ReactiveNeo4jRepository<MusicTrackDto, UUID> {

    @Query("""
            MATCH (musicTrackDto:Track)
            WHERE toLower(musicTrackDto.title) CONTAINS toLower($searchQuery)
            OPTIONAL MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicTrackDto:Track)
            WITH musicTrackDto, COUNT(r) AS isLiked
            RETURN musicTrackDto{
                .uuid,
                .title,
                .releaseYear,
                .bitrate,
                .lengthInMilliseconds,
                isLiked: CASE WHEN isLiked > 0 THEN true ELSE false END,
            MusicTrackDto_MUSIC_ALBUM_InnerAlbumDto: [(musicTrackDto)<-[:CONTAINS]-(musicTrackDto_musicAlbum:Album) | musicTrackDto_musicAlbum {
                    .description,
                    .name,
                    .uuid,
                    .yearOfRecord
                }],
            MusicTrackDto_MUSIC_BAND_InnerBandDto: [(musicTrackDto)<-[:HAS_TRACK]-(musicTrackDto_musicBand:Band) | musicTrackDto_musicBand {
                    .description,
                    .name,
                    .uuid
                }]
            }
                        
            UNION
            MATCH (musicTrackDto:Track)<-[:HAS_TRACK]-(musicTrackDto_musicBand:Band)
            WHERE toLower(musicTrackDto_musicBand.name) CONTAINS toLower($searchQuery)
            OPTIONAL MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicTrackDto:Track)
            WITH musicTrackDto, COUNT(r) AS isLiked, musicTrackDto_musicBand
            RETURN musicTrackDto{
                .uuid,
                .title,
                .releaseYear,
                .bitrate,
                .lengthInMilliseconds,
                isLiked: CASE WHEN isLiked > 0 THEN true ELSE false END,
            MusicTrackDto_MUSIC_ALBUM_InnerAlbumDto: [(musicTrackDto)<-[:CONTAINS]-(musicTrackDto_musicAlbum:Album) | musicTrackDto_musicAlbum {
                    .description,
                    .name,
                    .uuid,
                    .yearOfRecord
                }],
            MusicTrackDto_MUSIC_BAND_InnerBandDto: [musicTrackDto_musicBand {
                    .description,
                    .name,
                    .uuid
                }]
            }
            """)
    Flux<MusicTrackDto> findAllByName(String userUuid, String searchQuery);
}

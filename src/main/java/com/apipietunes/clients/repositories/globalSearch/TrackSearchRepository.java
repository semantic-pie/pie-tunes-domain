package com.apipietunes.clients.repositories.globalSearch;

import com.apipietunes.clients.models.dtos.domain.MusicTrackDto;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface TrackSearchRepository extends ReactiveNeo4jRepository<MusicTrackDto, UUID> {

    @Query("""
            MATCH (musicTrackDto:Track)
            WHERE toLower(musicTrackDto.title) CONTAINS toLower($searchQuery)
            OPTIONAL MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicTrackDto:Track)
            MATCH (musicTrackDto)<-[has_track:HAS_TRACK]-(musicTrackDto_musicBand:Band)
            MATCH (musicTrackDto)<-[contains:CONTAINS]-(musicTrackDto_musicAlbum:Album)
            WITH musicTrackDto,collect(has_track) as has_track, collect(contains) as contains,
            collect(musicTrackDto_musicBand) as musicTrackDto_musicBand,
            collect(musicTrackDto_musicAlbum) as musicTrackDto_musicAlbum,
            COUNT(r) AS isLiked
            RETURN musicTrackDto{
                .uuid,
                .title,
                .releaseYear,
                .bitrate,
                .lengthInMilliseconds,
                isLiked: CASE WHEN isLiked > 0 THEN true ELSE false END
            }, has_track, contains, musicTrackDto_musicBand, musicTrackDto_musicAlbum
            UNION
            MATCH (musicTrackDto:Track)<-[has_track:HAS_TRACK]-(musicTrackDto_musicBand:Band)
            WHERE toLower(musicTrackDto_musicBand.name) CONTAINS toLower($searchQuery)
            OPTIONAL MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicTrackDto:Track)
            MATCH (musicTrackDto)<-[contains:CONTAINS]-(musicTrackDto_musicAlbum:Album)
            WITH musicTrackDto, COUNT(r) AS isLiked,
            collect(contains) as contains,
            collect(musicTrackDto_musicBand) as musicTrackDto_musicBand,
            collect(musicTrackDto_musicAlbum) as musicTrackDto_musicAlbum,
            collect(has_track) as has_track
            RETURN musicTrackDto{
                .uuid,
                .title,
                .releaseYear,
                .bitrate,
                .lengthInMilliseconds,
                isLiked: CASE WHEN isLiked > 0 THEN true ELSE false END
            }, musicTrackDto_musicBand, musicTrackDto_musicAlbum, has_track, contains
    """)
    Flux<MusicTrackDto> findAllByName(String userUuid, String searchQuery);
}

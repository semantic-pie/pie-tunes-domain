package com.apipietunes.clients.repositories.neo4j.globalSearch;

import com.apipietunes.clients.models.dtos.globalSearch.TrackSearchDto;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface TrackSearchRepository extends ReactiveNeo4jRepository<TrackSearchDto, UUID> {

    @Query("""
            MATCH (trackSearchDto:Track)
            WHERE toLower(trackSearchDto.title) CONTAINS toLower($searchQuery)
            OPTIONAL MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(trackSearchDto:Track)
            WITH trackSearchDto, COUNT(r) AS isLiked
            RETURN trackSearchDto{
                .uuid,
                .title,
                .releaseYear,
                .bitrate,
                .lengthInMilliseconds,
                isLiked: CASE WHEN isLiked > 0 THEN true ELSE false END,
            TrackSearchDto_ALBUM_AlbumSearchDto: [(trackSearchDto)<-[:CONTAINS]-(trackSearchDto_album:Album) | trackSearchDto_album {
                    .description,
                    .isLiked,
                    .name,
                    .uuid,
                    .yearOfRecord
                }],
            TrackSearchDto_BAND_BandSearchDto: [(trackSearchDto)<-[:HAS_TRACK]-(trackSearchDto_band:Band) | trackSearchDto_band {
                    .description,
                    .isLiked,
                    .name,
                    .uuid
                }]
            }
            LIMIT 4
            """)
    Flux<TrackSearchDto> findAllByName(String userUuid, String searchQuery);
}

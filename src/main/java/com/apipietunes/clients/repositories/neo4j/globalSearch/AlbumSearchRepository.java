package com.apipietunes.clients.repositories.neo4j.globalSearch;

import com.apipietunes.clients.models.dtos.globalSearch.AlbumSearchDto;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface AlbumSearchRepository extends ReactiveNeo4jRepository<AlbumSearchDto, UUID> {

    @Query("""
            MATCH (albumSearchDto:Album)
            WHERE toLower(albumSearchDto.name) CONTAINS toLower($searchQuery)
            OPTIONAL MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(albumSearchDto:Album)
            WITH albumSearchDto, COUNT(r) AS isLiked
            RETURN albumSearchDto{
                .uuid,
                .name,
                .description,
                .yearOfRecord,
                isLiked: CASE WHEN isLiked > 0 THEN true ELSE false END,
                AlbumSearchDto_BAND_BandSearchDto: [(albumSearchDto)<-[:HAS_ALBUM]-(albumSearchDto_bands:Band) | albumSearchDto_bands {
                    .description,
                    .isLiked,
                    .name,
                    .uuid
                }]
            }
            LIMIT 4
            """)
    Flux<AlbumSearchDto> findAllByName(String userUuid, String searchQuery);
}
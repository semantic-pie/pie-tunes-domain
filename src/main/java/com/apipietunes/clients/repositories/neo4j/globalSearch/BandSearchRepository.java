package com.apipietunes.clients.repositories.neo4j.globalSearch;

import com.apipietunes.clients.models.dtos.globalSearch.BandSearchDto;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface BandSearchRepository extends ReactiveNeo4jRepository<BandSearchDto, UUID> {

    @Query("""
            MATCH (bandSearchDto:Band)
            WHERE toLower(bandSearchDto.name) CONTAINS toLower($searchQuery)
            OPTIONAL MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(bandSearchDto:Band)
            WITH bandSearchDto, COUNT(r) AS isLiked
            RETURN bandSearchDto{
                .uuid,
                .name,
                .description,
                isLiked: CASE WHEN isLiked > 0 THEN true ELSE false END
            }
            LIMIT 4
            """)
    Flux<BandSearchDto> findAllByName(String userUuid, String searchQuery);
}

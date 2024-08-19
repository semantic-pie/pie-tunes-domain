package com.apipietunes.clients.repository.globalsearch;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.apipietunes.clients.model.dto.domain.MusicBandDto;

import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface BandSearchRepository extends ReactiveNeo4jRepository<MusicBandDto, UUID> {

    @Query("""
            MATCH (musicBandDto:Band)
            WHERE toLower(musicBandDto.name) CONTAINS toLower($searchQuery)
            OPTIONAL MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicBandDto:Band)
            WITH musicBandDto, COUNT(r) AS isLiked
            RETURN musicBandDto{
                .uuid,
                .name,
                .description,
                isLiked: CASE WHEN isLiked > 0 THEN true ELSE false END
            }
            LIMIT 4
            """)
    Flux<MusicBandDto> findAllByName(String userUuid, String searchQuery);
}

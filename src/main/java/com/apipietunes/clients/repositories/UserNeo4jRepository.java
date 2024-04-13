package com.apipietunes.clients.repositories;


import com.apipietunes.clients.models.UserNeo4j;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserNeo4jRepository extends ReactiveNeo4jRepository<UserNeo4j, UUID> {
    Mono<UserNeo4j> findUserNeo4jByUuid(UUID uuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})
            OPTIONAL MATCH (t:Track {uuid: $trackUuid})
            CREATE (u)-[r:LIKES]->(t)
            SET r.createdAt = timestamp()
            RETURN u
            """)
    Mono<Void> likeExistingTrack(String trackUuid, String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})
            OPTIONAL MATCH (u)-[r:LIKES]->(t:Track {uuid: $trackUuid})
            RETURN COUNT(r) > 0
            """)
    Mono<Boolean> isLikeRelationExists(String trackUuid, String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid}) - [r:LIKES] -> (t:Track {uuid: $trackUuid})
            DELETE r
            """)
    Mono<Void> deleteLikeRelation(String trackUuid, String userUuid);

}
package com.apipietunes.clients.repository;


import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.apipietunes.clients.model.entity.UserNeo4j;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserNeo4jRepository extends ReactiveNeo4jRepository<UserNeo4j, UUID> {
    Mono<UserNeo4j> findUserNeo4jByUuid(UUID uuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})
            OPTIONAL MATCH (entity {uuid: $entityUuid})
            CREATE (u)-[r:LIKES]->(entity)
            SET r.createdAt = timestamp()
            RETURN u
            """)
    Mono<Void> likeExistingTrack(String entityUuid, String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})
            OPTIONAL MATCH (u)-[r:LIKES]->(entity {uuid: $entityUuid})
            RETURN COUNT(r) > 0
            """)
    Mono<Boolean> isLikeRelationExists(String entityUuid, String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid}) - [r:LIKES] -> (entity {uuid: $entityUuid})
            DELETE r
            """)
    Mono<Void> deleteLikeRelation(String entityUuid, String userUuid);

}
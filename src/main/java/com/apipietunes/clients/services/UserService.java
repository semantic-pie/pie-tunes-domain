package com.apipietunes.clients.services;

import com.apipietunes.clients.models.UserNeo4j;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;


public interface UserService {

    Mono<UserNeo4j> saveUserNeo4j(UUID userUuid);

    Mono<UserNeo4j> addPreferredGenres(Set<String> preferredGenres, UUID userUuid);

    Mono<Void> likeEntityEvent(String trackUuid, String userUuid);

    Mono<Void> removeLikeEvent(String trackUuid, String userUuid);
}

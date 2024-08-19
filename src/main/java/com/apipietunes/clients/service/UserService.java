package com.apipietunes.clients.service;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

import com.apipietunes.clients.model.entity.UserNeo4j;


public interface UserService {

    Mono<UserNeo4j> saveUserNeo4j(UUID userUuid);

    Mono<UserNeo4j> addPreferredGenres(Set<String> preferredGenres, UUID userUuid);

    Mono<Void> likeEntityEvent(String trackUuid, String userUuid);

    Mono<Void> removeLikeEvent(String trackUuid, String userUuid);
}

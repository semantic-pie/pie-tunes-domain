package com.apipietunes.clients.services;

import com.apipietunes.clients.models.UserNeo4j;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;


public interface UserService {

    Mono<UserNeo4j> createUser(UUID userUuid);

    Mono<UserNeo4j> addPreferredGenres(Set<String> preferredGenres, UUID userUuid);

    Mono<Void> likeTrackEvent(String trackUuid, String userUuid, ServerWebExchange exchange);

    Mono<Void> removeLikeEvent(String trackUuid, String userUuid, ServerWebExchange exchange);
}

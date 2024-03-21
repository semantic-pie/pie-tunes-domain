package com.apipietunes.clients.services;

import com.apipietunes.clients.models.dtos.UserSignUpRequest;
import com.apipietunes.clients.models.neo4jDomain.UserNeo4j;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public interface UserService {

    Mono<UserNeo4j> createUser(Mono<UserSignUpRequest> creationRequest);

    Mono<Void> likeTrackEvent(String trackUuid, String userUuid, ServerWebExchange exchange);

    Mono<Void> removeLikeEvent(String trackUuid, String userUuid, ServerWebExchange exchange);
}

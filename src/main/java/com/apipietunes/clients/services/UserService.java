package com.apipietunes.clients.services;

import com.apipietunes.clients.models.dtos.SaveUserUuidRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public interface UserService {

    Mono<Void> createUser(SaveUserUuidRequest creationRequest);

    Mono<Void> likeTrackEvent(String trackUuid, String userUuid, ServerWebExchange exchange);

    Mono<Void> removeLikeEvent(String trackUuid, String userUuid, ServerWebExchange exchange);
}

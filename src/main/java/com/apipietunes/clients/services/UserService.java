package com.apipietunes.clients.services;

import com.apipietunes.clients.models.dtos.UserSignUpRequest;
import com.apipietunes.clients.models.neo4jDomain.UserNeo4j;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserNeo4j> createUser(Mono<UserSignUpRequest> creationRequest);
}

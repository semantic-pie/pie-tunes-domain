package com.apipietunes.clients.services.impl;

import com.apipietunes.clients.models.dtos.UserSignUpRequest;
import com.apipietunes.clients.models.neo4jDomain.MusicGenre;
import com.apipietunes.clients.models.neo4jDomain.UserNeo4j;
import com.apipietunes.clients.models.sql.UserSql;
import com.apipietunes.clients.repositories.h2.UserH2Repository;
import com.apipietunes.clients.repositories.neo4j.MusicGenreRepository;
import com.apipietunes.clients.repositories.neo4j.UserNeo4jRepository;
import com.apipietunes.clients.services.UserService;
import com.apipietunes.clients.services.exceptions.ActionEventException;
import com.apipietunes.clients.services.exceptions.UserAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserNeo4jRepository userNeo4jRepository;
    private final UserH2Repository userH2Repository;
    private final MusicGenreRepository musicGenreRepository;

    @Override
    @Transactional
    public Mono<UserNeo4j> createUser(Mono<UserSignUpRequest> creationRequest) {
        return creationRequest.flatMap(request -> userH2Repository.findUserSqlByEmail(request.getEmail())
                .flatMap(existingUser -> {
                    String errorMessage = String.format("User with email '%s' already exists.", existingUser.getEmail());
                    log.info(errorMessage);
                    return Mono.error(new UserAlreadyExistsException(errorMessage));
                })
                .switchIfEmpty(Mono.defer(() -> saveUserToSql(request)))
                .cast(UserSql.class)
                .flatMap(savedUser -> saveUserToNeo4j(request, savedUser)));
    }

    private Mono<UserSql> saveUserToSql(UserSignUpRequest request) {
        var userSaveToSql = new UserSql(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole());
        return userH2Repository.save(userSaveToSql)
                .doOnNext(savedUser -> log.info("User saved to SQL database with UUID: {}", savedUser.getUuid()));
    }

    private Mono<UserNeo4j> saveUserToNeo4j(UserSignUpRequest request, UserSql savedSqlUser) {
        var userSaveToNeo4j = new UserNeo4j(savedSqlUser.getUuid());
        Set<String> favoriteGenres = request.getFavoriteGenres();
        return Flux.fromIterable(favoriteGenres)
                .flatMap(genre -> musicGenreRepository.persist(new MusicGenre(genre)))
                .collectList()
                .flatMap(persistedGenres -> {
                    persistedGenres.forEach(userSaveToNeo4j::addPreferredGenre);
                    return userNeo4jRepository.save(userSaveToNeo4j)
                            .doOnNext(savedUserNeo4j -> log.info("User saved to Neo4j database with UUID: {}", savedUserNeo4j.getUuid()));
                });
    }

    @Override
    @Transactional
    public Mono<Void> likeTrackEvent(String trackUuid, String userUuid, ServerWebExchange exchange) {
        return userNeo4jRepository.isLikeRelationExists(trackUuid, userUuid)
                .flatMap(isLikeExists -> {
                    if (!isLikeExists) {
                        return userNeo4jRepository.likeExistingTrack(trackUuid, userUuid)
                                .then();
                    } else {
                        String errorMessage = String.format("User already 'LIKES' track '%s'", trackUuid);
                        return Mono.error(new ActionEventException(errorMessage));
                    }
                });

    }

    @Override
    @Transactional
    public Mono<Void> removeLikeEvent(String trackUuid, String userUuid, ServerWebExchange exchange) {
        return userNeo4jRepository.isLikeRelationExists(trackUuid, userUuid)
                .flatMap(isLikeExists -> {
                    if (isLikeExists) {
                        return userNeo4jRepository.deleteLikeRelation(trackUuid, userUuid)
                                .then();
                    } else {
                        String errorMessage = String.format("User doesn't 'LIKE' track '%s'", trackUuid);
                        return Mono.error(new ActionEventException(errorMessage));
                    }
                });

    }


}






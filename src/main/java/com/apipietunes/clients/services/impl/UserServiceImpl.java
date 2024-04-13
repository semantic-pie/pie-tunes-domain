package com.apipietunes.clients.services.impl;

import com.apipietunes.clients.models.dtos.SaveUserUuidRequest;
import com.apipietunes.clients.models.UserNeo4j;
import com.apipietunes.clients.repositories.MusicGenreRepository;
import com.apipietunes.clients.repositories.UserNeo4jRepository;
import com.apipietunes.clients.services.UserService;
import com.apipietunes.clients.services.exceptions.ActionEventException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserNeo4jRepository userNeo4jRepository;
    private final MusicGenreRepository musicGenreRepository;

    @Override
    @Transactional
    public Mono<Void> createUser(SaveUserUuidRequest creationRequest) {
        var userNeo4j = new UserNeo4j(creationRequest.getUuid());
        return userNeo4jRepository.save(userNeo4j)
                .doOnSuccess(savedUser -> {
                    log.info("Saved to Neo4j database User with UUID: {}", savedUser.getUuid());
                })
                .then();
        /*Set<String> preferredGenres = creationRequest.getPreferredGenres();
        return Flux.fromIterable(preferredGenres)
                .flatMap(genre -> musicGenreRepository.persist(new MusicGenre(genre)))
                .collectList()
                .flatMap(persistedGenres -> {
                    persistedGenres.forEach(userSaveToNeo4j::addPreferredGenre);
                    return userNeo4jRepository.save(userSaveToNeo4j)
                            .map(UserNeo4j::getUuid)
                            .doOnSuccess(uuid -> log.info("Saved to SQL database User with UUID: {}", uuid));
                });*/
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






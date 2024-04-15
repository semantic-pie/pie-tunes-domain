package com.apipietunes.clients.services.impl;

import com.apipietunes.clients.models.MusicGenre;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;


@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserNeo4jRepository userNeo4jRepository;
    private final MusicGenreRepository musicGenreRepository;

    @Override
    @Transactional
    public Mono<UserNeo4j> saveUserNeo4j(UUID uuid) {
        return userNeo4jRepository.save(new UserNeo4j(uuid))
                .doOnSuccess(savedUser -> log.info("Saved to Neo4j database User with UUID: {}", savedUser.getUuid()));
    }

    @Override
    @Transactional
    public Mono<UserNeo4j> addPreferredGenres(Set<String> preferredGenres, UUID uuid) {
        return userNeo4jRepository.findUserNeo4jByUuid(uuid)
                .flatMap(existingUser -> {
                    Flux<MusicGenre> musicGenreFlux = Flux.fromIterable(preferredGenres)
                            .flatMap(genre -> musicGenreRepository.persist(new MusicGenre(genre)));

                    return musicGenreFlux.collectList()
                            .flatMap(persistedGenres -> {
                                persistedGenres.forEach(existingUser::addPreferredGenre);
                                return userNeo4jRepository.save(existingUser)
                                        .doOnSuccess(updatedUser ->
                                                log.info("Add genres: {} to User with UUID: {}",
                                                        persistedGenres, updatedUser.getUuid()));
                            });
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






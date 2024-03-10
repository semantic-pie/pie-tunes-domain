package com.apipietunes.clients.services;

import com.apipietunes.clients.models.dtos.UserSignUpRequest;
import com.apipietunes.clients.models.neo4jDomain.MusicGenre;
import com.apipietunes.clients.models.neo4jDomain.UserNeo4j;
import com.apipietunes.clients.models.sql.UserSql;
import com.apipietunes.clients.repositories.h2.UserH2Repository;
import com.apipietunes.clients.repositories.neo4j.MusicGenreRepository;
import com.apipietunes.clients.repositories.neo4j.UserNeo4jRepository;
import com.apipietunes.clients.services.exceptions.NodeAlreadyExists;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserNeo4jService implements UserService {

    private final UserNeo4jRepository userNeo4jRepository;
    private final UserH2Repository userH2Repository;
    private final MusicGenreRepository musicGenreRepository;
//
//    @Transactional
//    public Mono<Client> createClient(Mono<Client> client) {
//        return client.flatMap(clientRepository::save);
//    }
//
//    public Mono<Client> get(String name) {
//        return clientRepository.findClientByEmail(name);
//    }


    @Transactional
    public Mono<UserNeo4j> saveUserNeo4j(Mono<UserNeo4j> userMono) {

        return userMono
                .flatMap(user ->
                        userNeo4jRepository.findUserNeo4jByUuid(user.getUuid())
                                .flatMap(existingUser -> {
                                    String errorMessage = String.format("User with uuid '%s' already exists.", user.getUuid());
                                    log.info(errorMessage);
                                    return Mono.error(new NodeAlreadyExists(errorMessage));
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    Mono<UserNeo4j> toSave = userNeo4jRepository.save(user);
                                    log.info("Save user with UUID: {}.", user.getUuid());
                                    return toSave;
                                }))
                                .cast(UserNeo4j.class)
                );


    }


    public Mono<UserNeo4j> getUserByUuid(UUID uuid) {
        return userNeo4jRepository.findUserNeo4jByUuid(uuid);
    }

    @Override
    @Transactional
    public Mono<UserNeo4j> createUser(Mono<UserSignUpRequest> creationRequest) {

        return creationRequest
                .flatMap(request -> {
                    var userSaveToSql = new UserSql(
                            request.getName(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getRole());
                    return userH2Repository.save(userSaveToSql)
                            .flatMap(savedUser -> {
                                log.info("User saved to SQL database with UUID: {}", savedUser.getUuid());
                                var userSaveToNeo4j = new UserNeo4j(savedUser.getUuid());
                                Set<String> favoriteGenres = request.getFavoriteGenres();
                                return Flux.fromIterable(favoriteGenres)
                                        .flatMap(genre -> musicGenreRepository.persist(new MusicGenre(genre)))
                                        .collectList()
                                        .flatMap(persistedGenres -> {
                                            persistedGenres.forEach(userSaveToNeo4j::addPreferredGenre);

                                            return userNeo4jRepository.save(userSaveToNeo4j);
                                        });
                            });




                });
    }


    //        return user.flatMap(userNeo4jRepository::save);
}


//    public Mono<UserNeo4j> updateClient(String id, UserNeo4j updatedUser) {
//        return clientRepository.findClientByEmail(id)
//                .map(userToUpdate -> {
////                    userToUpdate.setName(updatedUser.getName());
////                    userToUpdate.setUserRole(updatedUser.getUserRole());
//                    return userToUpdate;
//                })
//                .flatMap(clientRepository::save);
//    }





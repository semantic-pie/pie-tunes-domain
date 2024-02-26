package com.apipietunes.clients.services;

import com.apipietunes.clients.models.neo4jDomain.UserNeo4j;
import com.apipietunes.clients.repositories.UserNeo4jRepository;
import com.apipietunes.clients.services.exceptions.UserAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserNeo4jService {

    private final UserNeo4jRepository userNeo4jRepository;
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
                                    return Mono.error(new UserAlreadyExistsException(errorMessage));
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



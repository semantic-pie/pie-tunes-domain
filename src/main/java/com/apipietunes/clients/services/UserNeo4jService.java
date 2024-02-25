package com.apipietunes.clients.services;

import com.apipietunes.clients.models.neo4jDomain.UserNeo4j;
import com.apipietunes.clients.repositories.UserNeo4jRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
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

    public Mono<UserNeo4j> saveUserNeo4j(Mono<UserNeo4j> user) {
        return user.flatMap(userNeo4jRepository::save);
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

}

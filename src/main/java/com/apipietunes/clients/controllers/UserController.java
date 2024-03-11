package com.apipietunes.clients.controllers;


import com.apipietunes.clients.models.dtos.UserSignUpRequest;
import com.apipietunes.clients.models.neo4jDomain.UserNeo4j;
import com.apipietunes.clients.repositories.neo4j.MusicGenreRepository;
import com.apipietunes.clients.services.UserService;
import com.apipietunes.clients.services.impl.UserServiceImpl;
import com.apipietunes.clients.services.exceptions.NodeAlreadyExists;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/domain/users")
public class UserController {

    private final UserService userService;


//        @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
   /* @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserNeo4j> save(@RequestBody Mono<UserNeo4j> client) {

        return userNeo4jService.saveUserNeo4j(client)
                .onErrorResume(error -> {
                    if (error instanceof NodeAlreadyExists) {
                        return Mono.empty();
                    } else {
                        return Mono.empty();
                    }
                });
    }*/


    /*@GetMapping("/{id}")
    public Mono<UserNeo4j> get(@PathVariable("id") String uuid) {
        return userNeo4jService.getUserByUuid(UUID.fromString(uuid));
    }*/

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserNeo4j> save(@RequestBody Mono<UserSignUpRequest> requestMono) {
        return userService.createUser(requestMono);
    }



}

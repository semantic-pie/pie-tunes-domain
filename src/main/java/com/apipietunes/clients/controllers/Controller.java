package com.apipietunes.clients.controllers;


import com.apipietunes.clients.models.neo4jDomain.UserNeo4j;
import com.apipietunes.clients.services.UserNeo4jService;
import com.apipietunes.clients.services.exceptions.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/usersMicroservice/users")
public class Controller {

    private UserNeo4jService userNeo4jService;


//        @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserNeo4j> save(@RequestBody Mono<UserNeo4j> client) {

        return userNeo4jService.saveUserNeo4j(client)
                .onErrorResume(error -> {
                    if (error instanceof UserAlreadyExistsException) {
                        return Mono.empty();
                    } else {
                        return Mono.empty();
                    }
                });
    }

//    @PutMapping("/{id}")
//    @PostMapping()
//    public Mono<UserNeo4j> update(@PathVariable("id") String id, @RequestBody UserNeo4j user) {
//        return userNeo4jService.updateClient(id, user);
//    }

    @GetMapping("/{id}")
    public Mono<UserNeo4j> get(@PathVariable("id") String uuid) {
        return userNeo4jService.getUserByUuid(UUID.fromString(uuid));
    }



}

package com.apipietunes.clients.controllers;


import com.apipietunes.clients.models.neo4jDomain.UserNeo4j;
import com.apipietunes.clients.services.UserNeo4jService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/usersMicroservice/users")
public class Cont {

    private UserNeo4jService userNeo4jService;


    //    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserNeo4j> save(@RequestBody Mono<UserNeo4j> client) {
        return userNeo4jService.saveUserNeo4j(client);
    }

//    @PutMapping("/{id}")
//    @PostMapping()
//    public Mono<UserNeo4j> update(@PathVariable("id") String id, @RequestBody UserNeo4j user) {
//        return userNeo4jService.updateClient(id, user);
//    }


}

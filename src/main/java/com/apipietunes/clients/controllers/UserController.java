package com.apipietunes.clients.controllers;


import com.apipietunes.clients.models.dtos.UserSignUpRequest;
import com.apipietunes.clients.models.neo4jDomain.UserNeo4j;
import com.apipietunes.clients.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@AllArgsConstructor
@CrossOrigin(origins = "origins", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST}, allowedHeaders = {"*"})
@RequestMapping(path = "/api/v1/domain/users")
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserNeo4j> save(@RequestBody Mono<UserSignUpRequest> requestMono) {
        return userService.createUser(requestMono);
    }


}

package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.messages.ApiPieTunesMessageInfo;
import com.apipietunes.clients.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/domain/users")
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiPieTunesMessageInfo> saveUserUuid(@RequestBody UUID userUuid, ServerWebExchange exchange) {
        return userService.createUser(userUuid)
                .map(savedUser -> {
                    String responseMessage =
                            String.format("Saved to Neo4j database User with UUID '%s'", savedUser.getUuid());
                    return new ApiPieTunesMessageInfo(HttpStatus.CREATED.value(),
                            exchange.getRequest().getPath().toString(), responseMessage);
                });


    }

    @PostMapping(value = "/{uuid}/addGenres", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiPieTunesMessageInfo> addPreferredGenresToUser(@PathVariable(name = "uuid") UUID userUuid,
                                                                 @RequestBody Set<String> preferredGenres,
                                                                 ServerWebExchange exchange) {

        return userService.addPreferredGenres(preferredGenres, userUuid)
                .map(updatedUser -> {
                    String responseMessage =
                            String.format("Add genres: '%s' to User with UUID: '%s'", preferredGenres, updatedUser.getUuid());
                    return new ApiPieTunesMessageInfo(HttpStatus.CREATED.value(),
                            exchange.getRequest().getPath().toString(), responseMessage);
                });
    }


}

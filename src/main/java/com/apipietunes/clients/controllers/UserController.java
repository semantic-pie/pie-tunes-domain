package com.apipietunes.clients.controllers;


import com.apipietunes.clients.models.dtos.SaveUserUuidRequest;
import com.apipietunes.clients.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1/domain/users")
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> saveUserUuid(@RequestBody SaveUserUuidRequest request) {
        return userService.createUser(request);
    }


}

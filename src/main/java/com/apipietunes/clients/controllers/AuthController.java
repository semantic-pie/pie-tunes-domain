package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.JwtResponse;
import com.apipietunes.clients.models.dtos.AuthRequest;
import com.apipietunes.clients.models.dtos.UserSignUpRequest;
import com.apipietunes.clients.services.AuthService;
import com.apipietunes.clients.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping(path = "/api/v1/authorisation")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<JwtResponse>> createUser(@RequestBody UserSignUpRequest request) {
        return authService.saveUserSql(request)
                .flatMap(savedSql -> userService.saveUserNeo4j(savedSql.getUuid()))
                .then(authService.authenticate(request.getEmail(), request.getPassword()));
    }


    @PostMapping("/login")
    public Mono<ResponseEntity<JwtResponse>> authentication(@RequestBody AuthRequest authRequest) {
        return authService.authenticate(authRequest.email(), authRequest.password());
    }



}

package com.apipietunes.clients.controller;

import com.apipietunes.clients.model.dto.AuthRequest;
import com.apipietunes.clients.model.dto.JwtResponse;
import com.apipietunes.clients.model.dto.UserSignUpRequest;
import com.apipietunes.clients.service.AuthService;
import com.apipietunes.clients.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping(path = "/api/v1/auth")
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

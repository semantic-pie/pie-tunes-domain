package com.apipietunes.clients.service;

import org.springframework.http.ResponseEntity;

import com.apipietunes.clients.model.dto.JwtResponse;
import com.apipietunes.clients.model.dto.UserSignUpRequest;
import com.apipietunes.clients.model.entity.UserSql;

import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<UserSql> saveUserSql(UserSignUpRequest creationRequest);

    Mono<ResponseEntity<JwtResponse>> authenticate(String email, String password);

}

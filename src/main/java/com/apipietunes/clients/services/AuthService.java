package com.apipietunes.clients.services;

import com.apipietunes.clients.models.JwtResponse;
import com.apipietunes.clients.models.UserSql;
import com.apipietunes.clients.models.dtos.UserSignUpRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<UserSql> saveUserSql(UserSignUpRequest creationRequest);

    Mono<ResponseEntity<JwtResponse>> authenticate(String email, String password);

}

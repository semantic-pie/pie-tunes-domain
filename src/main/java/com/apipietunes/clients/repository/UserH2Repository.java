package com.apipietunes.clients.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.apipietunes.clients.model.entity.UserSql;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserH2Repository extends R2dbcRepository<UserSql, UUID> {

    Mono<UserSql> findUserSqlByEmail(String email);
}

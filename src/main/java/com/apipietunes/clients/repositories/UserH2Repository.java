package com.apipietunes.clients.repositories;

import com.apipietunes.clients.models.UserSql;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserH2Repository extends R2dbcRepository<UserSql, UUID> {

    Mono<UserSql> findUserSqlByEmail(String email);
}

package com.apipietunes.clients.repositories.h2;

import com.apipietunes.clients.models.sql.UserSql;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserH2Repository extends R2dbcRepository<UserSql, UUID> {
}

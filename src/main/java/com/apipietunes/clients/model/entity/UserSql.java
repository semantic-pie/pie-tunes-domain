package com.apipietunes.clients.model.entity;


import lombok.*;
import org.springframework.data.annotation.Id;

import com.apipietunes.clients.model.enums.UserRole;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class UserSql {

    @Id
    private UUID uuid;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private UserRole role;

}

package com.apipietunes.clients.models;


import com.apipietunes.clients.models.enums.UserRole;
import lombok.*;
import org.springframework.data.annotation.Id;

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

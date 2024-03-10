package com.apipietunes.clients.models.sql;


import com.apipietunes.clients.models.sql.enums.UserRole;
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
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private UserRole role;


}

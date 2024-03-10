package com.apipietunes.clients.models.dtos;

import com.apipietunes.clients.models.sql.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequest {

    private String email;
    private String name;
    private String password;
    private UserRole role;
    private Set<String> favoriteGenres;

}

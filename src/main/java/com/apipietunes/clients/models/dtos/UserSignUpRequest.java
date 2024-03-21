package com.apipietunes.clients.models.dtos;

import com.apipietunes.clients.models.sql.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("favorite_genres")
    private Set<String> favoriteGenres;

}

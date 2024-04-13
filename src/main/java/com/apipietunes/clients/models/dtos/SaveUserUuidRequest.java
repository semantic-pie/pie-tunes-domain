package com.apipietunes.clients.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SaveUserUuidRequest {

    private UUID uuid;
}

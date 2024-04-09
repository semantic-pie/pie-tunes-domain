package com.apipietunes.clients.models.dtos.domain.inner;


import java.util.UUID;

public record InnerAlbumDto(UUID uuid, String name, String description, int yearOfRecord) {
}

package com.apipietunes.clients.models.refactoredDtos;


import java.util.UUID;

public record InnerAlbumDto(UUID uuid, String name, String description, int yearOfRecord) {
}

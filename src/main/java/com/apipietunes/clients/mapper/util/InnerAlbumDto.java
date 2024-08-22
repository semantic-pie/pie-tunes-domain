package com.apipietunes.clients.mapper.util;

import java.util.UUID;

public record InnerAlbumDto(UUID uuid, String name, String description, int yearOfRecord) {
}

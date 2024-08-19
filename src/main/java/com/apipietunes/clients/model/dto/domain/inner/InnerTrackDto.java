package com.apipietunes.clients.model.dto.domain.inner;

import java.util.UUID;

public record InnerTrackDto(UUID uuid, String title, String releaseYear, Integer bitrate, Long lengthInMilliseconds) {
}

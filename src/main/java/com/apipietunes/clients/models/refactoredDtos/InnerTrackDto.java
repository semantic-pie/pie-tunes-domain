package com.apipietunes.clients.models.refactoredDtos;


import java.util.UUID;


public record InnerTrackDto(UUID uuid, String title, String releaseYear, Integer bitrate, Long lengthInMilliseconds) {


}

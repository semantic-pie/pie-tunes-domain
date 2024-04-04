package com.apipietunes.clients.models.dtos;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrackLoaderResponseDto {
    private final MusicTrack uploadedTrack;
}

package com.apipietunes.clients.models.dtos;


import com.apipietunes.clients.models.refactoredDtos.MusicTrackDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrackLoaderResponseDto {
    private final MusicTrackDto uploadedTrack;
}

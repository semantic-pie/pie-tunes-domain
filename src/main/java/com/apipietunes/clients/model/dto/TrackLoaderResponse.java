package com.apipietunes.clients.model.dto;


import com.apipietunes.clients.model.dto.domain.MusicTrackDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrackLoaderResponse {
    private final MusicTrackDto uploadedTrack;
}

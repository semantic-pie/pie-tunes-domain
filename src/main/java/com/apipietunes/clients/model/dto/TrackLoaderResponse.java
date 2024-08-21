package com.apipietunes.clients.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrackLoaderResponse {
    private final MusicTrackDto uploadedTrack;
}

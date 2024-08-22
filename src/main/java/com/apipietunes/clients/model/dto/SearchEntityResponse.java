package com.apipietunes.clients.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SearchEntityResponse {
    private List<MusicTrackDto> tracks;
    private List<MusicAlbumDto> albums;
    private List<MusicBandDto> bands;
}

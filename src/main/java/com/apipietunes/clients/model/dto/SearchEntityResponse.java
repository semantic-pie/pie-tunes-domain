package com.apipietunes.clients.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.apipietunes.clients.model.dto.domain.MusicAlbumDto;
import com.apipietunes.clients.model.dto.domain.MusicBandDto;
import com.apipietunes.clients.model.dto.domain.MusicTrackDto;

@Getter
@Setter
@NoArgsConstructor
public class SearchEntityResponse {
    private List<MusicTrackDto> tracks;
    private List<MusicAlbumDto> albums;
    private List<MusicBandDto> bands;
}

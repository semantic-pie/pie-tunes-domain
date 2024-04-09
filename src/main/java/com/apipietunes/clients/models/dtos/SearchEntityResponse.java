package com.apipietunes.clients.models.dtos;

import com.apipietunes.clients.models.dtos.domain.MusicAlbumDto;
import com.apipietunes.clients.models.dtos.domain.MusicBandDto;
import com.apipietunes.clients.models.dtos.domain.MusicTrackDto;
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

package com.apipietunes.clients.models.dtos.globalSearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SearchEntityResponse {

    private List<TrackSearchDto> tracks;
    private List<AlbumSearchDto> albums;
    private List<BandSearchDto> bands;

}

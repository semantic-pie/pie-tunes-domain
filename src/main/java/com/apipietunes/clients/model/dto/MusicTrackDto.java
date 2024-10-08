package com.apipietunes.clients.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.apipietunes.clients.mapper.util.InnerAlbumDto;
import com.apipietunes.clients.mapper.util.InnerBandDto;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MusicTrackDto {

    private UUID uuid;

    private String title;

    private String releaseYear;

    private Integer bitrate;

    private Long lengthInMilliseconds;

    private Boolean isLiked;

    private InnerAlbumDto musicAlbum;

    private InnerBandDto musicBand;
}

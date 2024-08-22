package com.apipietunes.clients.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.apipietunes.clients.mapper.util.InnerBandDto;
import com.apipietunes.clients.mapper.util.InnerTrackDto;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MusicAlbumDto {

    private UUID uuid;

    private String name;

    private String description;

    private Integer yearOfRecord;

    private Boolean isLiked;

    private InnerBandDto musicBand;

    private Set<InnerTrackDto> tracks;
}

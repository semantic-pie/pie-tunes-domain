package com.apipietunes.clients.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.apipietunes.clients.mapper.util.InnerAlbumDto;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MusicBandDto {

    private UUID uuid;

    private String name;

    private String description;

    private Boolean isLiked;

    private Set<InnerAlbumDto> albums;
}

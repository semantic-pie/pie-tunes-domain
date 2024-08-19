package com.apipietunes.clients.model.dto.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;

import com.apipietunes.clients.model.dto.domain.inner.InnerBandDto;
import com.apipietunes.clients.model.dto.domain.inner.InnerTrackDto;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MusicAlbumDto {

    @Id
    private UUID uuid;

    private String name;

    private String description;

    private int yearOfRecord;

    private Boolean isLiked;

    private InnerBandDto musicBand;

    private Set<InnerTrackDto> tracks;
}

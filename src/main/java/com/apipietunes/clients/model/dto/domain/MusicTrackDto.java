package com.apipietunes.clients.model.dto.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;

import com.apipietunes.clients.model.dto.domain.inner.InnerAlbumDto;
import com.apipietunes.clients.model.dto.domain.inner.InnerBandDto;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MusicTrackDto {

    @Id
    private UUID uuid;

    private String title;

    private String releaseYear;

    private Integer bitrate;

    private Long lengthInMilliseconds;

    private Boolean isLiked;

    private InnerAlbumDto musicAlbum;

    private InnerBandDto musicBand;
}

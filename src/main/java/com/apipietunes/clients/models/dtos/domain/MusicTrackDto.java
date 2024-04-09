package com.apipietunes.clients.models.dtos.domain;

import com.apipietunes.clients.models.dtos.domain.inner.InnerAlbumDto;
import com.apipietunes.clients.models.dtos.domain.inner.InnerBandDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;

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

package com.apipietunes.clients.models.dtos.globalSearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TrackSearchDto {

    @Id
    private UUID uuid;

    private String title;

    private String releaseYear;

    private Integer bitrate;

    private Long lengthInMilliseconds;

    private boolean isLiked;

    private AlbumSearchDto album;

    private BandSearchDto band;


}

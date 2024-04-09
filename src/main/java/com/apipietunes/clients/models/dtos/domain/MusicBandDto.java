package com.apipietunes.clients.models.dtos.domain;

import com.apipietunes.clients.models.dtos.domain.inner.InnerAlbumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MusicBandDto {

    @Id
    private UUID uuid;

    private String name;

    private String description;

    private Boolean isLiked;

    private Set<InnerAlbumDto> albums;
}

package com.apipietunes.clients.models.refactoredDtos;

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

    private boolean isLiked;

    private Set<InnerAlbumDto> albums;
}

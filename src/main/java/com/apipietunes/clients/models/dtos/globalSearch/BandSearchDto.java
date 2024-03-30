package com.apipietunes.clients.models.dtos.globalSearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BandSearchDto {

    @Id
    private UUID uuid;

    private String name;

    private String description;

    private boolean isLiked;
}

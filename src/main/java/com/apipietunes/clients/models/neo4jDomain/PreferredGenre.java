package com.apipietunes.clients.models.neo4jDomain;

import lombok.*;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class PreferredGenre {

    @RelationshipId
    private Long id;

    @TargetNode
    @NonNull
    private MusicGenre genre;

    @NonNull
    private Integer weight;
}

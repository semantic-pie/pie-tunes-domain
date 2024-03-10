package com.apipietunes.clients.models.neo4jDomain;

import lombok.*;

import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;
import java.util.UUID;

@Node("Genre")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MusicGenre {

//    @Id
//    @GeneratedValue
//    private UUID uuid;

    @Version
    private Long version;

    @Id
    @NonNull
    private String name;

    //  @Relationship(type = "IN_GENRE", direction = Relationship.Direction.INCOMING)
    //  private Set<MusicTrack> tracks;

}

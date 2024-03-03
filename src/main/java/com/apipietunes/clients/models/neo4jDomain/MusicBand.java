package com.apipietunes.clients.models.neo4jDomain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.lang.Nullable;

import java.util.Set;
import java.util.UUID;

@Node("Band")
@RequiredArgsConstructor
@Getter
@Setter
public class MusicBand {

    @Id
    @GeneratedValue
    private UUID uuid;

    @Version
    private Long version;

    private String name;

    @Nullable
    private String description;

    //  @Relationship(type = "HAS_TRACK", direction = Relationship.Direction.OUTGOING)
    //  private Set<MusicTrack> allTracksInBand;

    //  @Relationship(type = "HAS_ALBUM", direction = Relationship.Direction.OUTGOING)
    //  private Set<MusicAlbum> allAlbumsInBand;


}

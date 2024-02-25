package com.apipietunes.clients.models.neo4jDomain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;
import java.util.UUID;

@Node("Album")
@RequiredArgsConstructor
@Getter
@Setter
public class MusicAlbum {

    @Id
    @GeneratedValue
    private UUID uuid;

    @Version
    private Long version;

    private String name;

    private String description;

    private int yearOfRecord;

    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    private Set<TrackData> tracksInAlbum;

    @Relationship(type = "HAS_ALBUM", direction = Relationship.Direction.INCOMING)
    private MusicBand musicBand;
}

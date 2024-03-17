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

@Node("Track")
@RequiredArgsConstructor
@Getter
@Setter
public class MusicTrack {

    @Id
    @GeneratedValue
    private UUID uuid;

    @Version
    private Long version;

    private String title;

    private String releaseYear;

    private Integer bitrate;

    private Long lengthInMilliseconds;

    @Relationship(type = "IN_GENRE", direction = Relationship.Direction.OUTGOING)
    private Set<MusicGenre> genres;

    @Relationship(type = "HAS_TRACK", direction = Relationship.Direction.INCOMING)
    private MusicBand musicBand;

    @Relationship(type = "CONTAINS", direction = Relationship.Direction.INCOMING)
    private MusicAlbum musicAlbum;
}

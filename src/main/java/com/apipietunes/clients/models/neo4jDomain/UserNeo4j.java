package com.apipietunes.clients.models.neo4jDomain;

import lombok.*;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Node("User")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserNeo4j {

    @Id
    @NonNull
    private UUID uuid;

    @Version
    private Long version;

    @Relationship(type = "PREFERS_GENRE", direction = Relationship.Direction.OUTGOING)
    private Set<MusicGenre> preferredGenres;

    @Relationship(type = "LIKES", direction = Relationship.Direction.OUTGOING)
    private Set<TrackData> likedTracks;

    @Relationship(type = "HAS_PLAYLIST", direction = Relationship.Direction.OUTGOING)
    private List<Playlist> playlists;



}

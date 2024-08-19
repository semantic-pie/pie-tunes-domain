package com.apipietunes.clients.model.entity.relation;

import lombok.*;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import com.apipietunes.clients.model.entity.MusicGenre;

import java.util.Objects;

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

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreferredGenre that = (PreferredGenre) o;
        return Objects.equals(genre, that.genre);
    }

    public int hashCode() {
        final int PRIME = 51;
        int result = 1;
        final Object $name = this.getGenre();
        result = result * PRIME + $name.hashCode();
        return result;
    }
}

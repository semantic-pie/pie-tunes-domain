package com.apipietunes.clients.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Objects;


@Node("Genre")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MusicGenre {

    @Id
    @NonNull
    private String name;

    @Version
    @JsonIgnore
    private Long version;

    public String toString() {
        return name;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicGenre that = (MusicGenre) o;
        return Objects.equals(name, that.name);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + $name.hashCode();
        return result;
    }
}

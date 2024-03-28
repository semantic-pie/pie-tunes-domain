package com.apipietunes.clients.models.neo4jDomain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

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

}

package com.apipietunes.clients.models.dtos;

import com.apipietunes.clients.models.enums.EntityType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SearchEntityResponse {

    @Id
    private UUID uuid;

    @Property("entity_type")
    private EntityType entityType;

    private String name;

    @Property("band_name")
    private String bandName;

}

package com.apipietunes.clients.models.mappers.innerEntities;

import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.refactoredDtos.InnerBandDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface InnerBandMapper {

    @Named("bandToInner")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    InnerBandDto sourceToDestination(MusicBand source);
}

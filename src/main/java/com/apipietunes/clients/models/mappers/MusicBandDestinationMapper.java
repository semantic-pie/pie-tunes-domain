package com.apipietunes.clients.models.mappers;

import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.refactoredDtos.MusicBandDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MusicBandDestinationMapper {

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    MusicBandDto sourceToDestination(MusicBand source);
}

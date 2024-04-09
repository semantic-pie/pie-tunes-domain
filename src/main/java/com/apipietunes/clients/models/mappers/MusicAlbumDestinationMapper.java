package com.apipietunes.clients.models.mappers;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.refactoredDtos.MusicAlbumDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MusicAlbumDestinationMapper {

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "yearOfRecord", source = "yearOfRecord")
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "bandToInner")
    MusicAlbumDto sourceToDestination(MusicAlbum source);

}

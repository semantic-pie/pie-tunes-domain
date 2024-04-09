package com.apipietunes.clients.models.mappers.innerEntities;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.refactoredDtos.InnerAlbumDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface InnerAlbumMapper {

    @Named("albumToInner")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "yearOfRecord", source = "yearOfRecord")
    InnerAlbumDto sourceToDestination(MusicAlbum source);

}

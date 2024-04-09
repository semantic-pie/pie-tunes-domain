package com.apipietunes.clients.models.mappers.innerEntities;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.models.refactoredDtos.InnerTrackDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface InnerTrackMapper {

    @Named("trackToInner")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "releaseYear", source = "releaseYear")
    @Mapping(target = "bitrate", source = "bitrate")
    @Mapping(target = "lengthInMilliseconds", source = "lengthInMilliseconds")
    InnerTrackDto sourceToDestination(MusicTrack source);
}

package com.apipietunes.clients.models.mappers;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.models.refactoredDtos.MusicTrackDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MusicTrackDestinationMapper {

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "releaseYear", source = "releaseYear")
    @Mapping(target = "bitrate", source = "bitrate")
    @Mapping(target = "lengthInMilliseconds", source = "lengthInMilliseconds")
    @Mapping(target = "musicAlbum", source = "musicAlbum", qualifiedByName = "albumToInner")
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "bandToInner")
    MusicTrackDto sourceToDestination(MusicTrack source);

}
package com.apipietunes.clients.models.mappers;

import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.models.refactoredDtos.MusicTrackDto;
import org.mapstruct.Mapper;

@Mapper
public interface MusicTrackDestinationMapper {

    MusicTrackDto sourceToDestination(MusicTrack source);
    MusicTrack destinationToSource(MusicTrackDto destination);

}

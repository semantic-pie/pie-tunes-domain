package com.apipietunes.clients.models.mappers.impl;

import com.apipietunes.clients.models.mappers.InnerAlbumMapper;
import com.apipietunes.clients.models.mappers.InnerBandMapper;
import com.apipietunes.clients.models.mappers.MusicTrackDestinationMapper;
import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.models.refactoredDtos.InnerAlbumDto;
import com.apipietunes.clients.models.refactoredDtos.InnerBandDto;
import com.apipietunes.clients.models.refactoredDtos.MusicAlbumDto;
import com.apipietunes.clients.models.refactoredDtos.MusicTrackDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MusicTrackDestinationMapperImpl implements MusicTrackDestinationMapper {

    private final InnerAlbumMapper innerAlbumMapper;
    private final InnerBandMapper innerBandMapper;

    @Override
    public MusicTrackDto sourceToDestination(MusicTrack source) {
        if (source == null) {
            return null;
        }
        InnerAlbumDto innerAlbum = innerAlbumMapper.sourceToDestination(source.getMusicAlbum());
        InnerBandDto innerBand = innerBandMapper.sourceToDestination(source.getMusicBand());

        MusicTrackDto musicTrackDto = new MusicTrackDto();
        musicTrackDto.setUuid(source.getUuid());
        musicTrackDto.setTitle(source.getTitle());
        musicTrackDto.setReleaseYear(source.getReleaseYear());
        musicTrackDto.setBitrate(source.getBitrate());
        musicTrackDto.setLengthInMilliseconds(source.getLengthInMilliseconds());

        musicTrackDto.setMusicAlbum(innerAlbum);
        musicTrackDto.setMusicBand(innerBand);

        return musicTrackDto;
    }

    @Override
    public MusicTrack destinationToSource(MusicTrackDto destination) {
        return null;
    }
}

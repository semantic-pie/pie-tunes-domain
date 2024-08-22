package com.apipietunes.clients.mapper;

import com.apipietunes.clients.mapper.util.InnerBandDto;
import com.apipietunes.clients.mapper.util.InnerTrackDto;
import com.apipietunes.clients.model.dto.MusicAlbumDto;
import com.apipietunes.clients.model.entity.MusicAlbum;
import com.apipietunes.clients.model.entity.MusicBand;
import com.apipietunes.clients.model.entity.MusicTrack;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MusicAlbumMapper {

    @Named("innerBand")
    InnerBandDto innerBand(MusicBand source);

    @Named("innerTrack")
    InnerTrackDto innerTrack(MusicTrack source);

    @Mapping(target = "tracks", source = "tracks", qualifiedByName = "innerTrack")
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "innerBand")
    MusicAlbumDto musicAlbumToMusicAlbumDto(MusicAlbum source);

    // Useless mapper, will be deleted after synchronization with fronted
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "innerBand")
    MusicAlbumDto outerAlbumWithoutTracks(MusicAlbum source);
}

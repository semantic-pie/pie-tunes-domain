package com.apipietunes.clients.mapper;

import com.apipietunes.clients.model.dto.MusicTrackDto;
import com.apipietunes.clients.mapper.util.InnerAlbumDto;
import com.apipietunes.clients.mapper.util.InnerBandDto;
import com.apipietunes.clients.model.entity.MusicAlbum;
import com.apipietunes.clients.model.entity.MusicBand;
import com.apipietunes.clients.model.entity.MusicTrack;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MusicTrackMapper {

    @Named("innerAlbum")
    InnerAlbumDto innerAlbum(MusicAlbum source);

    @Named("innerBand")
    InnerBandDto innerBand(MusicBand source);

    @Mapping(target = "musicAlbum", source = "musicAlbum", qualifiedByName = "innerAlbum")
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "innerBand")
    MusicTrackDto musicTrackToMusicTrackDto(MusicTrack source);
}

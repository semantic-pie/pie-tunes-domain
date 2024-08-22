package com.apipietunes.clients.mapper;

import com.apipietunes.clients.model.dto.MusicBandDto;
import com.apipietunes.clients.mapper.util.InnerAlbumDto;
import com.apipietunes.clients.model.entity.MusicAlbum;
import com.apipietunes.clients.model.entity.MusicBand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MusicBandMapper {

    @Named("innerAlbum")
    InnerAlbumDto innerAlbum(MusicAlbum source);

    @Mapping(target = "albums", source = "albums", qualifiedByName = "innerAlbum")
    MusicBandDto musicBandToMusicBandDto(MusicBand source);

    // GIGA Useless mapper, will be deleted after synchronization with fronted
    @Named("bandWithoutAlbums")
    MusicBandDto outerBandWithoutAlbums(MusicBand source);
}

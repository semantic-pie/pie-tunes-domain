package com.apipietunes.clients.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.apipietunes.clients.model.dto.domain.MusicAlbumDto;
import com.apipietunes.clients.model.dto.domain.MusicBandDto;
import com.apipietunes.clients.model.dto.domain.MusicTrackDto;
import com.apipietunes.clients.model.dto.domain.inner.InnerAlbumDto;
import com.apipietunes.clients.model.dto.domain.inner.InnerBandDto;
import com.apipietunes.clients.model.dto.domain.inner.InnerTrackDto;
import com.apipietunes.clients.model.entity.MusicAlbum;
import com.apipietunes.clients.model.entity.MusicBand;
import com.apipietunes.clients.model.entity.MusicTrack;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DomainEntityMapper {

    @Named("innerBand")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    InnerBandDto innerBand(MusicBand source);

    @Named("innerAlbum")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "yearOfRecord", source = "yearOfRecord")
    InnerAlbumDto innerAlbum(MusicAlbum source);

    @Named("innerTrack")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "releaseYear", source = "releaseYear")
    @Mapping(target = "bitrate", source = "bitrate")
    @Mapping(target = "lengthInMilliseconds", source = "lengthInMilliseconds")
    InnerTrackDto innerTrack(MusicTrack source);

    @Named("bandWithoutAlbums")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    MusicBandDto outerBandWithoutAlbums(MusicBand source);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "yearOfRecord", source = "yearOfRecord")
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "innerBand")
    MusicAlbumDto outerAlbumWithoutTracks(MusicAlbum source);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "releaseYear", source = "releaseYear")
    @Mapping(target = "bitrate", source = "bitrate")
    @Mapping(target = "lengthInMilliseconds", source = "lengthInMilliseconds")
    @Mapping(target = "musicAlbum", source = "musicAlbum", qualifiedByName = "innerAlbum")
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "innerBand")
    MusicTrackDto outerTrack(MusicTrack source);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "albums", source = "albums", qualifiedByName = "innerAlbum")
    MusicBandDto outerBand(MusicBand source);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "yearOfRecord", source = "yearOfRecord")
    @Mapping(target = "tracks", source = "tracks", qualifiedByName = "innerTrack")
    @Mapping(target = "musicBand", source = "musicBand", qualifiedByName = "innerBand")
    MusicAlbumDto outerAlbum(MusicAlbum source);
}

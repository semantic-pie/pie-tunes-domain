package com.apipietunes.clients.util;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import com.apipietunes.clients.model.entity.MusicAlbum;
import com.apipietunes.clients.model.entity.MusicBand;
import com.apipietunes.clients.model.entity.MusicGenre;
import com.apipietunes.clients.model.entity.MusicTrack;
import com.apipietunes.clients.service.exception.CantParseTrackMetadataException;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;


@Component
public class TrackMetadataParser {

    public TrackMetadataParseResult parse(FilePart file) {
        try {
            File trackFile = File.createTempFile(file.filename(), ".tmp");

            file.transferTo(trackFile).block();
            Mp3File mp3file = new Mp3File(trackFile);

            ID3v2 id3v2 = mp3file.getId3v2Tag();

            MusicTrack musicTrack = parseMusicTrack(mp3file, id3v2);
            MusicAlbum musicAlbum = parseMusicAlbum(mp3file, id3v2);
            MusicBand musicBand = parseMusicBand(mp3file, id3v2);
            Set<MusicGenre> musicGenre = parseMusicGenre(mp3file, id3v2);

            musicTrack.setMusicAlbum(musicAlbum);
            musicTrack.setMusicBand(musicBand);
            musicTrack.setGenres(musicGenre);

            byte[] cover = id3v2.getAlbumImage();
            String coverMimeType = id3v2.getAlbumImageMimeType();

            return TrackMetadataParseResult.builder()
                    .musicTrack(musicTrack)
                    .cover(cover)
                    .coverMimeType(coverMimeType).build();
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            e.printStackTrace();
            throw new CantParseTrackMetadataException("Cant parse track from file: " + file.filename());
        }
    }

    private MusicTrack parseMusicTrack(Mp3File mp3file, ID3v2 id3v2) {
        MusicTrack musicTrack = new MusicTrack();

        musicTrack.setTitle(id3v2.getTitle());
        musicTrack.setBitrate(mp3file.getBitrate());
        musicTrack.setLengthInMilliseconds(mp3file.getLengthInMilliseconds());
        musicTrack.setReleaseYear(id3v2.getYear());

        return musicTrack;
    }

    private MusicAlbum parseMusicAlbum(Mp3File file, ID3v2 id3v2) {
        MusicAlbum musicAlbum = new MusicAlbum();

        musicAlbum.setName(id3v2.getAlbum());
        try {
            musicAlbum.setYearOfRecord(Integer.parseInt(id3v2.getYear()));
        } catch (RuntimeException ex) {
        }

        return musicAlbum;
    }

    private MusicBand parseMusicBand(Mp3File file, ID3v2 id3v2) {
        MusicBand musicBand = new MusicBand();

        musicBand.setName(id3v2.getArtist());

        return musicBand;
    }

    private Set<MusicGenre> parseMusicGenre(Mp3File file, ID3v2 id3v2) {
        var genres = id3v2.getGenreDescription();
        if (genres == null || genres.isEmpty())
            return Set.of();
        return splitGenres(genres).map(this::toMusicGenre).collect(Collectors.toSet());
    }

    private MusicGenre toMusicGenre(String name) {
        MusicGenre musicGenre = new MusicGenre();
        musicGenre.setName(name);
        return musicGenre;
    }

    private Stream<String> splitGenres(String rowGenres) {
        return Arrays.stream(rowGenres.split("/")).map(this::unifySpaces).map(String::toLowerCase);
    }

    private String unifySpaces(String genre) {
        return genre
                .replace(' ', '-')
                .replace('_', '-');
    }
}

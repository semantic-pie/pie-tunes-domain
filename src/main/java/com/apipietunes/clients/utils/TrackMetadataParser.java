package com.apipietunes.clients.utils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.neo4jDomain.MusicGenre;
import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

@Component
public class TrackMetadataParser {

    public MusicTrack parse(FilePart file) {
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

            return musicTrack;
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            e.printStackTrace();
            throw new CantParseTrackMetadataException("kek");
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
        return splitGenres(id3v2.getGenreDescription()).map(this::toMusicGenre).collect(Collectors.toSet());
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

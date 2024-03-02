package com.apipietunes.clients.services;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.neo4jDomain.MusicGenre;
import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.repositories.MusicAlbumRepository;
import com.apipietunes.clients.repositories.MusicBandRepository;
import com.apipietunes.clients.repositories.MusicGenreRepository;
import com.apipietunes.clients.repositories.TrackMetadatRepository;
import com.apipietunes.clients.services.exceptions.NodeAlreadyExists;
import com.apipietunes.clients.utils.TrackMetadataParser;

import io.minio.MinioClient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class TrackLoaderServiceImpl implements TrackLoaderService {

    private final MinioClient minioClient;
    private final TrackMetadatRepository trackMetadatRepository;
    private final MusicBandRepository musicBandRepository;
    private final MusicGenreRepository musicGenreRepository;
    private final MusicAlbumRepository musicAlbumRepository;

    private final TrackMetadataParser parser;

    @Override
    public Mono<Void> saveAll(List<FilePart> trackFiles) {
        Queue<FilePart> tracksQueue = new LinkedList<>(trackFiles);
        return recursiveSave(save(tracksQueue.remove()), tracksQueue).then(Mono.empty());
    }

    private Mono<MusicTrack> recursiveSave(Mono<MusicTrack> track, Queue<FilePart> files) {
        if (files.size() > 0)
            return track.flatMap(t -> recursiveSave(save(files.remove()), files));
        else
            return track;
    }

    @Override
    @Transactional
    public Mono<MusicTrack> save(FilePart filePart) {
        try {
            MusicTrack musicTrack = parser.parse(filePart);

            Mono<MusicTrack> track = trackMetadatRepository
                    .findByTitleAndMusicBand_Name(
                            musicTrack.getTitle(),
                            musicTrack.getMusicBand().getName())
                    .flatMap((existingTrack) -> {
                        String errorMessage = String.format("Track with name '%s' and artist '%s' already exists.",
                                existingTrack.getTitle(), existingTrack.getMusicBand());
                        log.info(errorMessage);
                        return Mono.error(new NodeAlreadyExists(errorMessage));
                    })
                    .switchIfEmpty(saveNeo4j(musicTrack))
                    .cast(MusicTrack.class);

            return track;
        } catch (RuntimeException exception) {
            return Mono.error(exception);
        }
    }

    @Transactional
    protected Mono<MusicTrack> saveNeo4j(MusicTrack musicTrack) {
        log.info("Save track: {} - {}", musicTrack.getTitle(), musicTrack.getMusicBand().getName());

        MusicBand musicBand = musicTrack.getMusicBand();
        MusicAlbum musicAlbum = musicTrack.getMusicAlbum();
        Set<MusicGenre> musicGenres = musicTrack.getGenres();

        Mono<MusicBand> bandMono = musicBandRepository
                .findByName(musicBand.getName())
                .switchIfEmpty(Mono.defer(() -> musicBandRepository.save(musicBand)));

        Mono<MusicAlbum> albumMono = musicAlbumRepository
                .findByName(musicAlbum.getName())
                .switchIfEmpty(Mono.defer(() -> musicAlbumRepository.save(musicAlbum)));

        Flux<MusicGenre> genreFlux = Flux.fromIterable(musicGenres)
                .flatMap(g -> musicGenreRepository.findByName(g.getName())
                        .switchIfEmpty(Mono.defer(() -> musicGenreRepository.save(g))));

        return Mono.zip(bandMono, albumMono, genreFlux.collectList())
                .flatMap(tuple -> {
                    musicTrack.setMusicBand(tuple.getT1());
                    musicTrack.setMusicAlbum(tuple.getT2());
                    musicTrack.setGenres(new HashSet<>(tuple.getT3()));
                    return trackMetadatRepository.save(musicTrack);
                });
    }

}
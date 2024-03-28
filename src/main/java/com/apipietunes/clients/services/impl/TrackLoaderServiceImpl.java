package com.apipietunes.clients.services.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.neo4jDomain.MusicGenre;
import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.repositories.neo4j.MusicAlbumRepository;
import com.apipietunes.clients.repositories.neo4j.MusicBandRepository;
import com.apipietunes.clients.repositories.neo4j.MusicGenreRepository;
import com.apipietunes.clients.repositories.neo4j.MusicTrackRepository;
import com.apipietunes.clients.services.TrackLoaderService;
import com.apipietunes.clients.services.exceptions.NodeAlreadyExists;
import com.apipietunes.clients.utils.TrackMetadataParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackLoaderServiceImpl implements TrackLoaderService {

    @Autowired
    TrackLoaderServiceImpl self;

    @Value("${minio.buckets.tracks}")
    public String TRACKS_BUCKET;

    @Value("${minio.buckets.covers}")
    public String COVERS_BUCKET;

    private final MinioClient minioClient;
    private final MusicTrackRepository musicTrackRepository;
    private final MusicBandRepository musicBandRepository;
    private final MusicGenreRepository musicGenreRepository;
    private final MusicAlbumRepository musicAlbumRepository;

    private final TrackMetadataParser parser;

    @Override
    public Mono<Void> saveAll(List<FilePart> trackFiles) {
        Queue<FilePart> tracksQueue = new LinkedList<>(trackFiles);
        return recursiveSave(self.save(tracksQueue.remove()), tracksQueue).then(Mono.empty());
    }

    private Mono<MusicTrack> recursiveSave(Mono<MusicTrack> track, Queue<FilePart> files) {
        if (!files.isEmpty())
            return track.flatMap(t -> recursiveSave(self.save(files.remove()), files));
        else
            return track;
    }

    @Override
    @Transactional
    public Mono<MusicTrack> save(FilePart filePart) {
        try {
            var result = parser.parse(filePart);
            MusicTrack musicTrack = result.getMusicTrack();
            musicTrack.setReleaseYear("1988");

            return musicTrackRepository
                    .findByTitleAndMusicBand_Name(
                            musicTrack.getTitle(),
                            musicTrack.getMusicBand().getName())
                    .flatMap((existingTrack) -> {
                        String errorMessage = String.format("Track with name '%s' and artist '%s' already exists.",
                                existingTrack.getTitle(), existingTrack.getMusicBand().getName());
                        return Mono.error(new NodeAlreadyExists(errorMessage));
                    })
                    .switchIfEmpty(
                            saveNeo4j(musicTrack)
                                    .flatMap(persistedTrack -> saveMinio(persistedTrack, filePart, result.getCover(),
                                            result.getCoverMimeType())))
                    .cast(MusicTrack.class);
        } catch (RuntimeException exception) {
            return Mono.error(exception);
        }
    }

    protected Mono<MusicTrack> saveNeo4j(MusicTrack musicTrack) {
        log.info("Save track: {} - {}", musicTrack.getTitle(), musicTrack.getMusicBand().getName());

        MusicBand musicBand = musicTrack.getMusicBand();
        MusicAlbum musicAlbum = musicTrack.getMusicAlbum();

        Set<MusicGenre> musicGenres = musicTrack.getGenres();

        Mono<MusicBand> bandMono = musicBandRepository
                .findMusicBandByName(musicBand.getName())
                .switchIfEmpty(Mono.defer(() -> 
                    musicBandRepository.save(musicBand)));

        Mono<MusicAlbum> albumMono = musicAlbumRepository
                .findMusicAlbumByName(musicAlbum.getName())
                .switchIfEmpty(Mono.defer(() -> musicAlbumRepository.save(musicAlbum)));

        Flux<MusicGenre> genreFlux = Flux.fromIterable(musicGenres)
                .flatMap(g -> musicGenreRepository.findMusicGenreByName(g.getName())
                        .switchIfEmpty(Mono.defer(() -> musicGenreRepository.save(g))));

        return Mono.zip(bandMono, albumMono, genreFlux.collectList())
                .flatMap(tuple -> {
                    var band = tuple.getT1();
                    var album = tuple.getT2();

                    musicTrack.setMusicBand(band);
                    musicTrack.setMusicAlbum(album);

                    album.setMusicBand(band);

                    musicTrack.setGenres(new HashSet<>(tuple.getT3()));

                    return musicTrackRepository.save(musicTrack);
                });
    }

    private Mono<MusicTrack> saveMinio(MusicTrack musicTrack, FilePart file, byte[] cover, String coverContentType) {
        String trackContentType = Objects.requireNonNull(file.headers().getContentType()).toString();
        String trackObjectName = musicTrack.getUuid().toString();
        String coverObjectName = musicTrack.getMusicAlbum().getUuid().toString();

        return DataBufferUtils.join(file.content())
                .flatMap(dataBuffer -> {
                    log.info("Save track to MinIO '{}' : '{}'", musicTrack.getTitle(), trackObjectName);

                    try (
                            InputStream trackInputStream = dataBuffer.asInputStream();
                            InputStream coverInputStream = new ByteArrayInputStream(cover)) {
                        return Mono.zip(
                                // save track data
                                Mono.just(minioClient.putObject(
                                        PutObjectArgs.builder()
                                                .bucket(TRACKS_BUCKET)
                                                .object(trackObjectName)
                                                .contentType(trackContentType)
                                                .stream(trackInputStream, trackInputStream.available(), -1)
                                                .build())),
                                // save track cover if not exist
                                isCoverExist(coverObjectName).switchIfEmpty(Mono.defer(() -> {
                                            try {
                                                return Mono.just(minioClient.putObject(
                                                        PutObjectArgs.builder()
                                                                .bucket(COVERS_BUCKET)
                                                                .object(coverObjectName)
                                                                .contentType(coverContentType)
                                                                .stream(coverInputStream, coverInputStream.available(), -1)
                                                                .build()));
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        })))
                                .flatMap((ignore) -> Mono.just(musicTrack));
                    } catch (Exception ex) {
                        return Mono.empty();
                    }
                });
    }

    public Mono<GenericResponse> isCoverExist(String name) {
        try {
            return Mono.just(minioClient.statObject(StatObjectArgs.builder()
                    .bucket(COVERS_BUCKET)
                    .object(name).build()));
        } catch (ErrorResponseException e) {
            return Mono.empty();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void logAllAboutTrack(MusicTrack musicTrack) {
        log.info("title: {}", musicTrack.getTitle());
        log.info("releaseYear: {}", musicTrack.getReleaseYear());
        log.info("bitrate: {}", musicTrack.getBitrate());
        log.info("lengthInMilliseconds: {}", musicTrack.getLengthInMilliseconds());
        log.info("version: {}", musicTrack.getVersion());
        log.info("uuid: {}", musicTrack.getUuid());
        log.info("\n---- genres ----");
        for (MusicGenre musicGenre : musicTrack.getGenres()) {
            log.info("name: {}", musicGenre.getName());
            log.info("version: {}\n", musicGenre.getVersion());
        }

        log.info("---- band ----");
        var band = musicTrack.getMusicBand();
        log.info("name: {}", band.getName());
        log.info("description: {}", band.getDescription());
        log.info("version: {}", band.getVersion());
        log.info("uuid: {}\n", band.getUuid());

        log.info("---- album ----");
        var album = musicTrack.getMusicAlbum();
        log.info("name: {}", album.getName());
        log.info("description: {}", album.getDescription());
        log.info("year: {}", album.getYearOfRecord());
        log.info("version: {}", album.getVersion());
        log.info("uuid: {}\n", album.getUuid());

        log.info("genres: {}", musicTrack.getGenres().stream().map(MusicGenre::getName).toList());
        log.info(": {}",
        musicTrack.getGenres().stream().map(MusicGenre::getName).toList());
    }
}
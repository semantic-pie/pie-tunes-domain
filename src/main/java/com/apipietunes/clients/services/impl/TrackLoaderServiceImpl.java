package com.apipietunes.clients.services.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

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
import com.apipietunes.clients.repositories.neo4j.TrackMetadataRepository;
import com.apipietunes.clients.services.TrackLoaderService;
import com.apipietunes.clients.services.exceptions.NodeAlreadyExists;
import com.apipietunes.clients.utils.TrackMetadataParser;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackLoaderServiceImpl implements TrackLoaderService {

    @Value("${minio.buckets.tracks}")
    public String TRACKS_BUCKET;

    @Value("${minio.buckets.covers}")
    public String COVERS_BUCKET;

    private final MinioClient minioClient;
    private final TrackMetadataRepository trackMetadataRepository;
    private final MusicBandRepository musicBandRepository;
    private final MusicGenreRepository musicGenreRepository;
    private final MusicAlbumRepository musicAlbumRepository;

    private final TrackMetadataParser parser;

    @Override
    @Transactional
    public Mono<Void> saveAll(List<FilePart> trackFiles) {
        Queue<FilePart> tracksQueue = new LinkedList<>(trackFiles);
        return recursiveSave(save(tracksQueue.remove()), tracksQueue).then(Mono.empty());
    }

    private Mono<MusicTrack> recursiveSave(Mono<MusicTrack> track, Queue<FilePart> files) {
        if (!files.isEmpty())
            return track.flatMap(t -> recursiveSave(save(files.remove()), files));
        else
            return track;
    }

    @Override
//    @Transactional
    public Mono<MusicTrack> save(FilePart filePart) {
       log.info("call save method");
        try {
            var result = parser.parse(filePart);
            MusicTrack musicTrack = result.getMusicTrack();
            musicTrack.setReleaseYear("1988");

            return trackMetadataRepository
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
    @Transactional
    private Mono<MusicTrack> saveNeo4j(MusicTrack musicTrack) {
        log.info("Save track: {} - {}", musicTrack.getTitle(), musicTrack.getMusicBand().getName());

        MusicBand musicBand = musicTrack.getMusicBand();
        MusicAlbum musicAlbum = musicTrack.getMusicAlbum();
        musicAlbum.setMusicBand(musicBand);
        Set<MusicGenre> musicGenres = musicTrack.getGenres();

        Mono<MusicBand> bandMono = musicBandRepository
                .findMusicBandByName(musicBand.getName())
                .switchIfEmpty(Mono.defer(() -> musicBandRepository.save(musicBand)));

        Mono<MusicAlbum> albumMono = musicAlbumRepository
                .findMusicAlbumByName(musicAlbum.getName())
                .switchIfEmpty(Mono.defer(() -> musicAlbumRepository.save(musicAlbum)));

        Flux<MusicGenre> genreFlux = Flux.fromIterable(musicGenres)
                .flatMap(g -> musicGenreRepository.findMusicGenreByName(g.getName())
                        .switchIfEmpty(Mono.defer(() -> musicGenreRepository.save(g))));

        return Mono.zip(bandMono, albumMono, genreFlux.collectList())
                .flatMap(tuple -> {
                    musicTrack.setMusicBand(tuple.getT1());
                    musicTrack.setMusicAlbum(tuple.getT2());
                    musicTrack.setGenres(new HashSet<>(tuple.getT3()));
                    return trackMetadataRepository.save(musicTrack);
                });
    }

    private Mono<MusicTrack> saveMinio(MusicTrack musicTrack, FilePart file, byte[] cover, String coverMimeType) {
        String contentType = file.headers().getContentType().toString();
        String filename = musicTrack.getUuid().toString();

        return DataBufferUtils.join(file.content())
                .flatMap(dataBuffer -> {
                    log.info("Save track to MinIO '{}' : '{}'", musicTrack.getTitle(), filename);

                    try (
                            InputStream trackInputStream = dataBuffer.asInputStream();
                            InputStream coverInputStream = new ByteArrayInputStream(cover)) {
                        return Mono.zip(
                                        // save track data
                                        Mono.just(minioClient.putObject(
                                                PutObjectArgs.builder()
                                                        .bucket(TRACKS_BUCKET)
                                                        .object(filename)
                                                        .contentType(contentType)
                                                        .stream(trackInputStream, trackInputStream.available(), -1)
                                                        .build())),
                                        // save track cover
                                        Mono.just(minioClient.putObject(
                                                PutObjectArgs.builder()
                                                        .bucket(COVERS_BUCKET)
                                                        .object(filename)
                                                        .contentType(coverMimeType)
                                                        .stream(coverInputStream, coverInputStream.available(), -1)
                                                        .build())))
                                .flatMap((ignore) -> Mono.just(musicTrack));
                    } catch (Exception ex) {
                        return Mono.empty();
                    }
                });
    }
}
package com.apipietunes.clients.services.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
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
    private final TrackMetadatRepository trackMetadatRepository;
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
    @Transactional
    public Mono<MusicTrack> save(FilePart filePart) {
        try {
            var result = parser.parse(filePart);
            MusicTrack musicTrack = result.getMusicTrack();

            Mono<MusicTrack> track = trackMetadatRepository
                    .findByTitleAndMusicBand_Name(
                            musicTrack.getTitle(),
                            musicTrack.getMusicBand().getName())
                    .flatMap((existingTrack) -> {
                        String errorMessage = String.format("Track with name '%s' and artist '%s' already exists.",
                                existingTrack.getTitle(), existingTrack.getMusicBand());
                        return Mono.error(new NodeAlreadyExists(errorMessage));
                    })
                    .switchIfEmpty(
                            saveNeo4j(musicTrack)
                                    .flatMap(persistedTrack -> saveMinio(persistedTrack, filePart, result.getCover(),
                                            result.getCoverMimeType())))
                    .cast(MusicTrack.class);

            return track;
        } catch (RuntimeException exception) {
            return Mono.error(exception);
        }
    }

    @Transactional
    private Mono<MusicTrack> saveNeo4j(MusicTrack musicTrack) {
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

    private Mono<MusicTrack> saveMinio(MusicTrack musicTrack, FilePart file, byte[] cover, String coverMimeType) {
        String contentType = file.headers().getContentType().toString();
        String filename = musicTrack.getUuid().toString();

        return DataBufferUtils.join(file.content())
                .flatMap(dataBuffer -> {
                    log.info("save to minio '{}' : '{}'", musicTrack.getTitle(), filename);

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
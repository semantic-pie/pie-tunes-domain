package com.apipietunes.clients.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.neo4jDomain.MusicGenre;
// import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
// import com.apipietunes.clients.models.neo4jDomain.MusicBand;
// import com.apipietunes.clients.models.neo4jDomain.MusicGenre;
import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.repositories.MusicAlbumRepository;
import com.apipietunes.clients.repositories.MusicBandRepository;
import com.apipietunes.clients.repositories.MusicGenreRepository;
import com.apipietunes.clients.repositories.TrackMetadatRepository;
import com.apipietunes.clients.services.exceptions.UserAlreadyExistsException;
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
                        return Mono.error(new UserAlreadyExistsException(errorMessage));
                    })
                    .switchIfEmpty(saveNeo4j(musicTrack))
                    .cast(MusicTrack.class);

            return track;
        } catch (RuntimeException exception) {
            return Mono.error(exception);
        }
    }


    @Transactional
    private Mono<MusicTrack> saveNeo4j(MusicTrack musicTrack) {
        log.info("Save track: {} - {}", musicTrack.getTitle(), musicTrack.getMusicBand().getName());
        // musicTrack.setUuid(UUID.randomUUID());
    
        return trackMetadatRepository.save(musicTrack)
                .flatMap(persistedTrack -> {
                    MusicBand musicBand = musicTrack.getMusicBand();
                    MusicAlbum musicAlbum = musicTrack.getMusicAlbum();
                    Set<MusicGenre> musicGenres = musicTrack.getGenres();
    
                    Mono<MusicBand> bandMono = musicBandRepository.save(musicBand);
                            // .existsByName(musicBand.getName())
                            // .flatMap(exsits -> {
                            //     if (exsits) {
                            //         log.info("FUUUUUCK FIND");
                            //         return musicBandRepository.findByName(musicBand.getName());
                            //     } else {
                            //         log.info("FUUUUUCK SAVE");
                            //         return musicBandRepository.save(musicBand);
                            //     }
                            // });
                            // .findByName(musicBand.getName())
                            // .switchIfEmpty(Mono.defer(() -> {
                            //     log.info("CREATING NEW ENTITY");
                            //     musicBand.setUuid(UUID.randomUUID());
                            //     return musicBandRepository.save(musicBand);
                            // }));
                    // log.info("FFFFF");
                    // log.info("BAND: {}", bandMono);
    
                    Mono<MusicAlbum> albumMono = musicAlbumRepository.save(musicAlbum);
                            // .findByName(musicAlbum.getName())
                            // .switchIfEmpty(Mono.defer(() -> {
                            //     musicAlbum.setUuid(UUID.randomUUID());
                            //     return musicAlbumRepository.save(musicAlbum);
                            // }));
                    
                    // log.info("ALBUM: {}", albumMono.block());
    
                    Flux<MusicGenre> genreFlux = musicGenreRepository.saveAll(musicGenres);
                    // Flux<MusicGenre> genreFlux = Flux.fromIterable(musicGenres).
                            // .flatMap(g -> musicGenreRepository.findByName(g.getName())
                            //         .switchIfEmpty(Mono.defer(() -> {
                            //             g.setUuid(UUID.randomUUID());
                            //             return musicGenreRepository.save(g);
                            //         })));
                        
                    // log.info("GENRE: {}", genreFlux.collectList());
                    
    
                    return Mono.zip(bandMono, albumMono, genreFlux.collectList())
                            .flatMap(tuple -> {
                                persistedTrack.setMusicBand(tuple.getT1());
                                persistedTrack.setMusicAlbum(tuple.getT2());
                                persistedTrack.setGenres(new HashSet<>(tuple.getT3()));
                                return trackMetadatRepository.save(persistedTrack);
                            });
                });
                // .flatMap(track -> trackMetadatRepository.findByUuid(musicTrack.getUuid()));
    }

    
    // @Transactional
    // private Mono<MusicTrack> saveNeo4jBlock(MusicTrack musicTrack) {
    //     log.info("Save track: {} - {}", musicTrack.getTitle(), musicTrack.getMusicBand().getName());

    //     musicTrack.setUuid(UUID.randomUUID());

    //     MusicBand musicBand = musicTrack.getMusicBand();
    //     MusicAlbum musicAlbum = musicTrack.getMusicAlbum();
    //     Set<MusicGenre> musicGenres = musicTrack.getGenres();

    //     MusicTrack persistedTrack = trackMetadatRepository.save(musicTrack).block();
    //     log.info("before: {}", persistedTrack);


    //     // MusicBand d = musicBandRepository.findByName(musicBand.getName()).block();
    //     // log.info("TEEEEEST TEEEEEAT ________________ {}", d);

    //     MusicBand band = musicBandRepository
    //             .findByName(musicBand.getName())
    //             .switchIfEmpty(Mono.defer(() -> {
    //                 musicBand.setUuid(UUID.randomUUID());
    //                 return musicBandRepository.save(musicBand);
    //             }))
    //             .block();
                
    //     MusicAlbum album = musicAlbumRepository
    //             .findByName(musicBand.getName())
    //             .switchIfEmpty(Mono.defer(() -> {
    //                 musicAlbum.setUuid(UUID.randomUUID());
    //                 return musicAlbumRepository.save(musicAlbum);
    //             }))
    //             .block();

    //     Set<MusicGenre> genres = musicGenres.stream()
    //             .map(g -> musicGenreRepository.findByName(g.getName())
    //                     .switchIfEmpty(Mono.defer(() -> {
    //                         g.setUuid(UUID.randomUUID());
    //                         return musicGenreRepository.save(g);
    //                     })).block())
    //             .collect(Collectors.toSet());

    //     persistedTrack.setMusicBand(band);
    //     persistedTrack.setMusicAlbum(album);
    //     persistedTrack.setGenres(genres);
    //     persistedTrack.setUuid(musicTrack.getUuid());

    //     log.info("after: {}", persistedTrack);

    //     // musicTrack.setUuid(UUID.randomUUID());

    //     return trackMetadatRepository.save(persistedTrack);
    // }

    // @Transactional
    // private Mono<MusicTrack> saveNeo4j(MusicTrack musicTrack) {
    // log.info("Save track: {} - {}", musicTrack.getTitle(),
    // musicTrack.getMusicBand().getName());
    // UUID uuid = UUID.randomUUID();
    // musicTrack.setUuid(uuid);

    // MusicBand musicBand = musicTrack.getMusicBand();
    // Set<MusicGenre> musicGenres = musicTrack.getGenres();
    // return trackMetadatRepository.save(musicTrack)
    // .flatMap(track -> musicBandRepository.findByName(musicBand.getName())
    // .switchIfEmpty(musicBandRepository.save(musicBand))
    // .flatMap(band -> Mono.just(musicGenres)
    // .flatMap(resolverGenres(musicGenres)
    // .flatMap((genres) -> {

    // track.setUuid(uuid);
    // track.setMusicBand(band);
    // track.setGenres(genres);
    // return trackMetadatRepository.save(track);
    // }))));
    // }

    // private Mono<Set<MusicGenre>> resolverGenres(Set<MusicGenre> genres) {
    // return Mono.empty();
    // }
}
// return Mono.defer(
// () -> musicBandRepository.save(musicTrack.getMusicBand()))
// .flatMap(band -> musicGenreRepository.saveAll(musicTrack.getGenres()).next())
// .flatMap(v -> trackMetadatRepository.save(musicTrack));

// .flatMap(band -> Mono.just(musicGenres)
// .flatMap(g -> g.stream()
// .flatMap(g ->
// musicGenreRepository.findByName(g.getName()).switchIfEmpty(musicGenreRepository.save(g)).toList())
// .flatMap((genres) -> {
// track.setUuid(uuid);
// track.setMusicBand(band);
// track.setGenres(new HashSet<>(genres));
// return trackMetadatRepository.save(track);
// }))));
package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.mappers.DomainEntityMapper;
import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.refactoredDtos.MusicAlbumDto;
import com.apipietunes.clients.repositories.neo4j.MusicAlbumRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api/v1/library/albums")
public class AlbumsController {

    private final MusicAlbumRepository musicAlbumRepository;
    private final DomainEntityMapper entityMapper;


    @Deprecated
    @GetMapping()
    public Flux<MusicAlbumDto> getMethodName(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "8") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        return musicAlbumRepository.findAllAlbums(pageable)
                .map(entityMapper::outerAlbumWithoutTracks);
    }

    @GetMapping("/{uuid}")
    @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Album uuid")
    public ResponseEntity<Mono<MusicAlbumDto>>
    findTrackByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok()
                .body(musicAlbumRepository.findMusicAlbumByUuid(uuid)
                        .map(entityMapper::outerAlbum));
    }

    @GetMapping("/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name = "order", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public ResponseEntity<Flux<MusicAlbumDto>>
    findAlbumsByDate(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "16") int limit,
                     @RequestParam(defaultValue = "desc") String order,
                     @RequestParam String userUuid) {

        Sort sort = Sort.by(Sort.Direction.fromString(order.toLowerCase()), "r.createdAt");
        Pageable pageable = PageRequest.of(page, limit, sort);

        Mono<Long> totalLikedAlbums =
                musicAlbumRepository.findTotalLikedAlbums(userUuid);

        Flux<MusicAlbum> allLikedAlbums =
                musicAlbumRepository.findAllLikedAlbums(userUuid, pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalLikedAlbums.block()))
                .body(allLikedAlbums.map(foundAlbum -> {
                    MusicAlbumDto albumDto = entityMapper.outerAlbumWithoutTracks(foundAlbum);
                    albumDto.setIsLiked(true);
                    return albumDto;
                }));
    }

    @GetMapping("/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public ResponseEntity<Flux<MusicAlbumDto>>
    findAlbumsByTitle(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "16") int limit,
                      @RequestParam(value = "q") String query,
                      @RequestParam String userUuid) {

        Pageable pageable = PageRequest.of(page, limit);

        Mono<Long> totalLikedAlbumsByTitle =
                musicAlbumRepository.findTotalLikedAlbumsByTitle(query.toLowerCase(), userUuid);

        Flux<MusicAlbum> allLikedAlbumsByTitle =
                musicAlbumRepository.findAllLikedAlbumsByTitle(query.toLowerCase(), userUuid, pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalLikedAlbumsByTitle.block()))
                .body(allLikedAlbumsByTitle.map(foundAlbum -> {
                    MusicAlbumDto albumDto = entityMapper.outerAlbumWithoutTracks(foundAlbum);
                    albumDto.setIsLiked(true);
                    return albumDto;
                }));


    }
}

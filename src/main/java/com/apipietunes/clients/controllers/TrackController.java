package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.mappers.DomainEntityMapper;
import com.apipietunes.clients.models.MusicTrack;
import com.apipietunes.clients.models.dtos.domain.MusicTrackDto;
import com.apipietunes.clients.repositories.MusicTrackRepository;
import com.apipietunes.clients.services.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api/v1/library/tracks")
public class TrackController {

    private final MusicTrackRepository musicTrackRepository;
    private final DomainEntityMapper entityMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Deprecated
    @GetMapping()
    public Flux<MusicTrackDto> getMethodName(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "8") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        return musicTrackRepository.findAllTracks(pageable)
                .map(entityMapper::outerTrack);
    }

    @GetMapping("/{uuid}")
    @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Track uuid")
    public ResponseEntity<Mono<MusicTrackDto>>
    findTrackByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok()
                .body(musicTrackRepository.findMusicTrackByUuid(uuid)
                        .map(entityMapper::outerTrack));
    }

    @GetMapping("/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name = "order", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public ResponseEntity<Flux<MusicTrackDto>>
    findTracksByDate(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "16") int limit,
                     @RequestParam(defaultValue = "desc") String order,
                     ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        Sort sort = Sort.by(Sort.Direction.fromString(order.toLowerCase()), "r.createdAt");
        Pageable pageable = PageRequest.of(page, limit, sort);

        Mono<Long> totalLikedTracks =
                musicTrackRepository.findTotalLikedTracks(userUuid);

        Flux<MusicTrack> allLikedTracks =
                musicTrackRepository.findAllLikedTracks(userUuid, pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalLikedTracks.block()))
                .body(allLikedTracks.map(foundTrack -> {
                    MusicTrackDto trackDto = entityMapper.outerTrack(foundTrack);
                    trackDto.setIsLiked(true);
                    return trackDto;
                }));
    }

    @GetMapping("/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public ResponseEntity<Flux<MusicTrackDto>>
    findTracksByTitle(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "16") int limit,
                      @RequestParam(value = "q") String query,
                      ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        Pageable pageable = PageRequest.of(page, limit);

        Mono<Long> totalLikedTracksByTitle =
                musicTrackRepository.findTotalLikedTracksByTitle(query.toLowerCase(), userUuid);

        Flux<MusicTrack> allLikedTracksByTitle =
                musicTrackRepository.findAllLikedTracksByTitle(query.toLowerCase(), userUuid, pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalLikedTracksByTitle.block()))
                .body(allLikedTracksByTitle.map(foundTrack -> {
                    MusicTrackDto trackDto = entityMapper.outerTrack(foundTrack);
                    trackDto.setIsLiked(true);
                    return trackDto;
                }));
    }

    @GetMapping("/find-by-album/{uuid}")
    @Operation(description = "Find all tracks of album with uuid.")
    @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Album uuid")
    public ResponseEntity<Flux<MusicTrackDto>>
    findTracksByAlbum(@PathVariable String uuid) {

        Mono<Long> totalTracksInAlbum =
                musicTrackRepository.findTotalTracksInAlbumByUuid(uuid);

        Flux<MusicTrack> allTracksInAlbum =
                musicTrackRepository.findTracksByAlbumUuid(uuid);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalTracksInAlbum.block()))
                .body(allTracksInAlbum.map(foundTrack -> {
                    MusicTrackDto trackDto = entityMapper.outerTrack(foundTrack);
                    trackDto.setIsLiked(true);
                    return trackDto;
                }));
    }
}

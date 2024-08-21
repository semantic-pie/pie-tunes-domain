package com.apipietunes.clients.controller;

import com.apipietunes.clients.mapper.MusicTrackMapper;
import com.apipietunes.clients.model.dto.MusicTrackDto;
import com.apipietunes.clients.model.entity.MusicTrack;
import com.apipietunes.clients.repository.MusicTrackRepository;
import com.apipietunes.clients.repository.UserNeo4jRepository;
import com.apipietunes.clients.service.jwt.JwtTokenProvider;

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

import java.util.UUID;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api/v1/library/tracks")
public class TrackController {

    private final MusicTrackRepository musicTrackRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MusicTrackMapper trackMapper;

    private final static String X_TOTAL_COUNT_HEADER = "X-Total-Count";
    private final static String SORT_BY_CREATED_AT = "r.createdAt";

    @Deprecated
    @GetMapping()
    public Flux<MusicTrackDto> getMethodName(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "8") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        return musicTrackRepository.findAllTracks(pageable)
                .map(trackMapper::musicTrackToMusicTrackDto);
    }

    @GetMapping("/{uuid}")
    @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Track uuid")
    public ResponseEntity<Mono<MusicTrackDto>>
    findTrackByUuid(@PathVariable String uuid, ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        return ResponseEntity.ok()
                .body(musicTrackRepository.findMusicTrackByUuid(UUID.fromString(uuid))
                        .map(trackMapper::musicTrackToMusicTrackDto)
                        .flatMap(bandDto ->
                                userNeo4jRepository.isLikeRelationExists(String.valueOf(bandDto.getUuid()), userUuid)
                                        .map(isLiked -> {
                                            bandDto.setIsLiked(isLiked);
                                            return bandDto;
                                        })));
    }

    @GetMapping("/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name = "order", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public ResponseEntity<Flux<MusicTrackDto>>
    findTracksByDate(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "16") int limit,
                     @RequestParam(defaultValue = "desc") String order,
                     ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        Sort sort = Sort.by(Sort.Direction.fromString(order.toLowerCase()), SORT_BY_CREATED_AT);
        Pageable pageable = PageRequest.of(page, limit, sort);

        Mono<Long> totalLikedTracks =
                musicTrackRepository.findTotalLikedTracks(userUuid);

        Flux<MusicTrackDto> allLikedTracks = musicTrackRepository.findAllLikedTracks(userUuid, pageable)
                .map(trackMapper::musicTrackToMusicTrackDto)
                .map(trackDto -> {
                    trackDto.setIsLiked(true);
                    return trackDto;
                });

        return ResponseEntity.ok()
                .header(X_TOTAL_COUNT_HEADER, String.valueOf(totalLikedTracks.block()))
                .body(allLikedTracks);
    }

    @GetMapping("/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public ResponseEntity<Flux<MusicTrackDto>>
    findTracksByTitle(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "16") int limit,
                      @RequestParam(value = "q") String query,
                      ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);
        String queryLowerCase = query.toLowerCase();

        Pageable pageable = PageRequest.of(page, limit);

        Mono<Long> totalLikedTracksByTitle =
                musicTrackRepository.findTotalLikedTracksByTitle(queryLowerCase, userUuid);

        Flux<MusicTrackDto> allLikedTracksByTitle = musicTrackRepository.findAllLikedTracksByTitle(queryLowerCase, userUuid, pageable)
                .map(trackMapper::musicTrackToMusicTrackDto)
                .map(trackDto -> {
                    trackDto.setIsLiked(true);
                    return trackDto;
                });

        return ResponseEntity.ok()
                .header(X_TOTAL_COUNT_HEADER, String.valueOf(totalLikedTracksByTitle.block()))
                .body(allLikedTracksByTitle);
    }

    // Egor: I don't see any sense of this endpoint. If u want to get all tracks in album u can use "/api/v1/library/albums/{uuid}"
    // in Albums controller. There I returned album, all it's tracks, band.
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
                .header(X_TOTAL_COUNT_HEADER, String.valueOf(totalTracksInAlbum.block()))
                .body(allTracksInAlbum.map(foundTrack -> {
                    MusicTrackDto trackDto = trackMapper.musicTrackToMusicTrackDto(foundTrack);
                    trackDto.setIsLiked(true);
                    return trackDto;
                }));
    }
}

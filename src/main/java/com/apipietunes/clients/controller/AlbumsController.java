package com.apipietunes.clients.controller;

import com.apipietunes.clients.mapper.MusicAlbumMapper;
import com.apipietunes.clients.model.dto.MusicAlbumDto;
import com.apipietunes.clients.repository.MusicAlbumRepository;
import com.apipietunes.clients.repository.UserNeo4jRepository;
import com.apipietunes.clients.service.jwt.JwtTokenProvider;

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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api/v1/library/albums")
public class AlbumsController {

    private final MusicAlbumRepository musicAlbumRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MusicAlbumMapper albumMapper;

    private final static String X_TOTAL_COUNT_HEADER = "X-Total-Count";
    private final static String SORT_BY_CREATED_AT = "r.createdAt";


    @Deprecated
    @GetMapping()
    public Flux<MusicAlbumDto> getMethodName(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "8") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        return musicAlbumRepository.findAllAlbums(pageable)
                .map(albumMapper::outerAlbumWithoutTracks);
    }

    @GetMapping("/{uuid}")
    @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Album uuid")
    public ResponseEntity<Mono<MusicAlbumDto>>
    findAlbumByUuid(@PathVariable String uuid, ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        return ResponseEntity.ok()
                .body(musicAlbumRepository.findMusicAlbumByUuid(UUID.fromString(uuid))
                        .map(albumMapper::musicAlbumToMusicAlbumDto)
                        .flatMap(albumDto ->
                                userNeo4jRepository.isLikeRelationExists(String.valueOf(albumDto.getUuid()), userUuid)
                                        .map(isLiked -> {
                                            albumDto.setIsLiked(isLiked);
                                            return albumDto;
                                        })));
    }

    @GetMapping("/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name = "order", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public ResponseEntity<Flux<MusicAlbumDto>>
    findAlbumsByDate(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "16") int limit,
                     @RequestParam(defaultValue = "desc") String order,
                     ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        Sort sort = Sort.by(Sort.Direction.fromString(order.toLowerCase()), SORT_BY_CREATED_AT);
        Pageable pageable = PageRequest.of(page, limit, sort);

        Mono<Long> totalLikedAlbums =
                musicAlbumRepository.findTotalLikedAlbums(userUuid);

        Flux<MusicAlbumDto> likedAlbums = musicAlbumRepository.findAllLikedAlbums(userUuid, pageable)
                .map(albumMapper::musicAlbumToMusicAlbumDto)
                .map(albumDto -> {
                    albumDto.setIsLiked(true);
                    return albumDto;
                });

        return ResponseEntity.ok()
                .header(X_TOTAL_COUNT_HEADER, String.valueOf(totalLikedAlbums.block()))
                .body(likedAlbums);
    }

    @GetMapping("/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public ResponseEntity<Flux<MusicAlbumDto>>
    findAlbumsByTitle(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "16") int limit,
                      @RequestParam(value = "q") String query,
                      ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);
        String queryLowerCase = query.toLowerCase();

        Pageable pageable = PageRequest.of(page, limit);

        Mono<Long> totalLikedAlbumsByTitle =
                musicAlbumRepository.findTotalLikedAlbumsByTitle(queryLowerCase, userUuid);

        Flux<MusicAlbumDto> allLikedAlbumsByTitle = musicAlbumRepository.findAllLikedAlbumsByTitle(queryLowerCase, userUuid, pageable)
                .map(albumMapper::musicAlbumToMusicAlbumDto)
                .map(albumDto -> {
                    albumDto.setIsLiked(true);
                    return albumDto;
                });

        return ResponseEntity.ok()
                .header(X_TOTAL_COUNT_HEADER, String.valueOf(totalLikedAlbumsByTitle.block()))
                .body(allLikedAlbumsByTitle);
    }

}

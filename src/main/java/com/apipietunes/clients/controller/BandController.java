package com.apipietunes.clients.controller;

import com.apipietunes.clients.mapper.MusicBandMapper;
import com.apipietunes.clients.model.dto.MusicBandDto;
import com.apipietunes.clients.repository.MusicBandRepository;
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
@RequestMapping("/api/v1/library/artists")
public class BandController {

    private final MusicBandRepository musicBandRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserNeo4jRepository userNeo4jRepository;
    private final MusicBandMapper bandMapper;

    private final static String X_TOTAL_COUNT_HEADER = "X-Total-Count";
    private final static String SORT_BY_CREATED_AT = "r.createdAt";

    @Deprecated
    @GetMapping()
    public Flux<MusicBandDto> getMethodName(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "8") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        return musicBandRepository.findAllBands(pageable)
                .map(bandMapper::outerBandWithoutAlbums);
    }


    @GetMapping("/{uuid}")
    @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Band uuid")
    public ResponseEntity<Mono<MusicBandDto>>
    findArtistByUuid(@PathVariable String uuid, ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        return ResponseEntity.ok()
                .body(musicBandRepository.findMusicBandByUuid(UUID.fromString(uuid))
                        .map(bandMapper::musicBandToMusicBandDto)
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
    public ResponseEntity<Flux<MusicBandDto>>
    findArtistsByDate(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "16") int limit,
                      @RequestParam(defaultValue = "desc") String order,
                      ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        Sort sort = Sort.by(Sort.Direction.fromString(order.toLowerCase()), SORT_BY_CREATED_AT);
        Pageable pageable = PageRequest.of(page, limit, sort);

        Mono<Long> totalLikedBands =
                musicBandRepository.findTotalLikedBands(userUuid);

        Flux<MusicBandDto> allLikedBands = musicBandRepository.findAllLikedBands(userUuid, pageable)
                .map(bandMapper::musicBandToMusicBandDto)
                .map(bandDto -> {
                    bandDto.setIsLiked(true);
                    return bandDto;
                });

        return ResponseEntity.ok()
                .header(X_TOTAL_COUNT_HEADER, String.valueOf(totalLikedBands.block()))
                .body(allLikedBands);
    }

    @GetMapping("/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public ResponseEntity<Flux<MusicBandDto>>
    findArtistsByTitle(@RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "16") int limit,
                @RequestParam(value = "q") String query,
                ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);
        String queryLowerCase = query.toLowerCase();

        Pageable pageable = PageRequest.of(page, limit);

        Mono<Long> totalLikedBandsByTitle =
                musicBandRepository.findTotalLikedBandsByTitle(queryLowerCase, userUuid);

        Flux<MusicBandDto> allLikedBandsByTitle = musicBandRepository.findAllLikedBandsByTitle(queryLowerCase, userUuid, pageable)
                .map(bandMapper::musicBandToMusicBandDto)
                .map(bandDto -> {
                    bandDto.setIsLiked(true);
                    return bandDto;
                });

        return ResponseEntity.ok()
                .header(X_TOTAL_COUNT_HEADER, String.valueOf(totalLikedBandsByTitle.block()))
                .body(allLikedBandsByTitle);
    }

}

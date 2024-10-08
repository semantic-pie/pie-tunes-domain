package com.apipietunes.clients.controller;

import com.apipietunes.clients.mapper.MusicAlbumMapper;
import com.apipietunes.clients.mapper.MusicBandMapper;
import com.apipietunes.clients.mapper.MusicTrackMapper;
import com.apipietunes.clients.model.dto.SearchEntityResponse;
import com.apipietunes.clients.model.dto.MusicAlbumDto;
import com.apipietunes.clients.model.dto.MusicBandDto;
import com.apipietunes.clients.model.dto.MusicTrackDto;
import com.apipietunes.clients.repository.MusicAlbumRepository;
import com.apipietunes.clients.repository.MusicBandRepository;
import com.apipietunes.clients.repository.MusicTrackRepository;
import com.apipietunes.clients.repository.UserNeo4jRepository;
import com.apipietunes.clients.service.jwt.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@Slf4j
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping(path = "/api/v1")
public class SearchController {

    private final JwtTokenProvider jwtTokenProvider;

    private final MusicAlbumRepository musicAlbumRepository;
    private final MusicTrackRepository musicTrackRepository;
    private final MusicBandRepository musicBandRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final MusicAlbumMapper albumMapper;
    private final MusicBandMapper bandMapper;
    private final MusicTrackMapper trackMapper;

    private final static int MAX_ENTITIES_COUNT = 4;

    @GetMapping("/search")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    public Mono<SearchEntityResponse>
    globalSearchItems(@RequestParam(value = "q") String searchQuery, ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);
        String queryLowerCase = searchQuery.toLowerCase();

        Flux<MusicTrackDto> musicTrackDto = musicTrackRepository.findAllByTitleContainingIgnoreCase(queryLowerCase)
                .take(MAX_ENTITIES_COUNT)
                .map(trackMapper::musicTrackToMusicTrackDto)
                .flatMap(trackDto ->
                        userNeo4jRepository.isLikeRelationExists(String.valueOf(trackDto.getUuid()), userUuid)
                                .map(isLiked -> {
                                    trackDto.setIsLiked(isLiked);
                                    return trackDto;
                                })
                );

        Flux<MusicAlbumDto> musicAlbumDto = musicAlbumRepository.findAllByNameContainingIgnoreCase(queryLowerCase)
                .take(MAX_ENTITIES_COUNT)
                .map(albumMapper::musicAlbumToMusicAlbumDto)
                .flatMap(albumDto ->
                        userNeo4jRepository.isLikeRelationExists(String.valueOf(albumDto.getUuid()), userUuid)
                                .map(isLiked -> {
                                    albumDto.setIsLiked(isLiked);
                                    return albumDto;
                                })
                );

        Flux<MusicBandDto> musicBandDto = musicBandRepository.findAllByNameContainingIgnoreCase(queryLowerCase)
                .take(MAX_ENTITIES_COUNT)
                .map(bandMapper::musicBandToMusicBandDto)
                .flatMap(bandDto ->
                        userNeo4jRepository.isLikeRelationExists(String.valueOf(bandDto.getUuid()), userUuid)
                                .map(isLiked -> {
                                    bandDto.setIsLiked(isLiked);
                                    return bandDto;
                                })
                );

        return Mono.zip(musicTrackDto.collectList(), musicAlbumDto.collectList(), musicBandDto.collectList())
                .map(tuple -> {
                    SearchEntityResponse searchEntityResponse = new SearchEntityResponse();
                    searchEntityResponse.setTracks(tuple.getT1());
                    searchEntityResponse.setAlbums(tuple.getT2());
                    searchEntityResponse.setBands(tuple.getT3());
                    return searchEntityResponse;
                });
    }

}

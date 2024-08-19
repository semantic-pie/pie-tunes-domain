package com.apipietunes.clients.controller;

import com.apipietunes.clients.model.dto.SearchEntityResponse;
import com.apipietunes.clients.model.dto.domain.MusicAlbumDto;
import com.apipietunes.clients.model.dto.domain.MusicBandDto;
import com.apipietunes.clients.model.dto.domain.MusicTrackDto;
import com.apipietunes.clients.repository.globalsearch.AlbumSearchRepository;
import com.apipietunes.clients.repository.globalsearch.BandSearchRepository;
import com.apipietunes.clients.repository.globalsearch.TrackSearchRepository;
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

    private final TrackSearchRepository trackSearchRepository;
    private final AlbumSearchRepository albumSearchRepository;
    private final BandSearchRepository bandSearchRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/search")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    public Mono<SearchEntityResponse>
    globalSearchItems(@RequestParam(value = "q") String searchQuery, ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        Flux<MusicTrackDto> tracks = trackSearchRepository.findAllByName(userUuid, searchQuery.toLowerCase()).take(4);
        Flux<MusicAlbumDto> albums = albumSearchRepository.findAllByName(userUuid, searchQuery.toLowerCase());
        Flux<MusicBandDto> bands = bandSearchRepository.findAllByName(userUuid, searchQuery.toLowerCase());

        return Mono.zip(tracks.collectList(), albums.collectList(), bands.collectList())
                .map(tuple -> {
                    SearchEntityResponse searchEntityResponse = new SearchEntityResponse();
                    searchEntityResponse.setTracks(tuple.getT1());
                    searchEntityResponse.setAlbums(tuple.getT2());
                    searchEntityResponse.setBands(tuple.getT3());
                    return searchEntityResponse;
                });
    }

}

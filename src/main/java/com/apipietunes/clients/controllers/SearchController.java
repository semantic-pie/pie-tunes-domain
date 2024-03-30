package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.dtos.globalSearch.AlbumSearchDto;
import com.apipietunes.clients.models.dtos.globalSearch.BandSearchDto;
import com.apipietunes.clients.models.dtos.globalSearch.SearchEntityResponse;
import com.apipietunes.clients.models.dtos.globalSearch.TrackSearchDto;
import com.apipietunes.clients.repositories.neo4j.globalSearch.AlbumSearchRepository;
import com.apipietunes.clients.repositories.neo4j.globalSearch.BandSearchRepository;
import com.apipietunes.clients.repositories.neo4j.globalSearch.TrackSearchRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/search")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    public Mono<SearchEntityResponse>
    globalSearchItems(@RequestParam(value = "q") String searchQuery, @RequestParam String userUuid) {

        Flux<TrackSearchDto> tracks = trackSearchRepository.findAllByName(userUuid, searchQuery);
        Flux<AlbumSearchDto> albums = albumSearchRepository.findAllByName(userUuid, searchQuery);
        Flux<BandSearchDto> bands = bandSearchRepository.findAllByName(userUuid, searchQuery);

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

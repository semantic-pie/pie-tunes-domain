package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.dtos.SearchEntityResponse;
import com.apipietunes.clients.repositories.neo4j.SearchItemsRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping(path = "/api/v1")
public class SearchController {

    private final SearchItemsRepository searchItemsRepository;

    @GetMapping("/search")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public Mono<ResponseEntity<Map<String, List<SearchEntityResponse>>>>
    globalSearchItems(@RequestParam(value = "q") String searchQuery,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "8") int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        List<SearchEntityResponse> albums = new ArrayList<>();
        List<SearchEntityResponse> bands = new ArrayList<>();
        List<SearchEntityResponse> tracks = new ArrayList<>();

        Mono<Long> totalAlbums = searchItemsRepository.findTotalAllMatchedAlbums(searchQuery);
        Mono<Long> totalBands = searchItemsRepository.findTotalAllMatchedBands(searchQuery);
        Mono<Long> totalTracks = searchItemsRepository.findTotalAllMatchedTracks(searchQuery);

        Flux<SearchEntityResponse> repositoryResponse = searchItemsRepository.findAllMatches(searchQuery, pageable);

        return Mono.zip(repositoryResponse.collectList(), totalAlbums, totalBands, totalTracks)
                .map(tuple -> {

                    for (SearchEntityResponse item : tuple.getT1()) {
                        String itemType = item.getEntityType().name();
                        switch (itemType) {
                            case "ALBUM":
                                albums.add(item);
                                break;
                            case "BAND":
                                bands.add(item);
                                break;
                            case "TRACK":
                                tracks.add(item);
                                break;
                            default:
                                break;
                        }
                    }

                    Map<String, List<SearchEntityResponse>> response = new HashMap<>();
                    response.put("albums", albums);
                    response.put("bands", bands);
                    response.put("tracks", tracks);
                    return ResponseEntity.ok()
                            .header("X-Total-Albums-Count", String.valueOf(tuple.getT2()))
                            .header("X-Total-Bands-Count", String.valueOf(tuple.getT3()))
                            .header("X-Total-Tracks-Count", String.valueOf(tuple.getT4()))
                            .body(response);
                });

    }

}

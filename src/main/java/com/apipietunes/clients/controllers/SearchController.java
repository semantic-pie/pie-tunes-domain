package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.dtos.SearchEntityResponse;
import com.apipietunes.clients.repositories.neo4j.SearchItemsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
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
    public Mono<Map<String, List<SearchEntityResponse>>> test(@RequestParam(required = false, value = "q") String searchQuery,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "8") int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        List<SearchEntityResponse> albums = new ArrayList<>();
        List<SearchEntityResponse> bands = new ArrayList<>();
        List<SearchEntityResponse> tracks = new ArrayList<>();

        return searchItemsRepository.findAllMatches(searchQuery, pageable)
                .collectList()
                .map(items -> {
                    for (SearchEntityResponse item : items) {
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
                    return response;
                });
    }

}

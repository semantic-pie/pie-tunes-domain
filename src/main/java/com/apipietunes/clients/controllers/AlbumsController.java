package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.dtos.SearchEntityResponse;
import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.repositories.neo4j.MusicAlbumRepository;
import com.apipietunes.clients.repositories.neo4j.SearchItemsRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api/v1/library/albums")
public class AlbumsController {

    private final MusicAlbumRepository musicAlbumRepository;
    private final SearchItemsRepository searchItemsRepository;

    @Deprecated
    @GetMapping()
    public Flux<MusicAlbum> getMethodName(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "8") int limit) {

        return musicAlbumRepository.findAll().skip(page).take(limit);
    }

    @GetMapping("/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name = "order", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public Mono<ResponseEntity<Map<String, List<SearchEntityResponse>>>>
    findAlbumsByDate(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "16") int limit,
                     @RequestParam(defaultValue = "16") String order,
                     @RequestParam String userUuid) {

        Sort sort = Sort.by(Sort.Direction.fromString(order.toLowerCase()), "r.createdAt");
        Pageable pageable = PageRequest.of(page, limit, sort);
        return searchItemsRepository.findAllLikedAlbums(userUuid, pageable)
                .collectList()
                .zipWith(searchItemsRepository.findTotalLikedAlbums(userUuid))
                .map(tuple -> {
                    Map<String, List<SearchEntityResponse>> response = new HashMap<>();
                    response.put("liked_albums", tuple.getT1());
                    long total = tuple.getT2();

                    return ResponseEntity.ok()
                            .header("X-Total-Count", String.valueOf(total))
                            .body(response);
                });
    }

    @GetMapping("/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public Mono<ResponseEntity<Map<String, List<SearchEntityResponse>>>>
    findAlbumsByTitle(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "16") int limit,
                      @RequestParam(value = "q") String query,
                      @RequestParam String userUuid) {

        Pageable pageable = PageRequest.of(page, limit);
        return searchItemsRepository.findAllLikedAlbumsByTitle(query, userUuid, pageable)
                .collectList()
                .zipWith(searchItemsRepository.findTotalLikedAlbumsByTitle(query, userUuid))
                .map(tuple -> {
                    Map<String, List<SearchEntityResponse>> response = new HashMap<>();
                    response.put("found_albums", tuple.getT1());
                    long total = tuple.getT2();

                    return ResponseEntity.ok()
                            .header("X-Total-Count", String.valueOf(total))
                            .body(response);
                });
    }
}

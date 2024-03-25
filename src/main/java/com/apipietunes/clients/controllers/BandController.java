package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.dtos.SearchEntityResponse;
import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.repositories.neo4j.MusicBandRepository;
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
@RequestMapping("/api/v1/library/artists")
public class BandController {

    private final MusicBandRepository musicBandRepository;
    private final SearchItemsRepository searchItemsRepository;

    @Deprecated
    @GetMapping()
    public Flux<MusicBand> getMethodName(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "8") int limit) {

        return musicBandRepository.findAll().skip(page).take(limit);
    }

    @GetMapping("/artists/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name = "order", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public Mono<ResponseEntity<Map<String, List<SearchEntityResponse>>>>
    findArtistsByDate(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "16") int limit,
                      @RequestParam(defaultValue = "16") String order,
                      @RequestParam String userUuid) {

        Sort sort = Sort.by(Sort.Direction.fromString(order.toLowerCase()), "r.createdAt");
        Pageable pageable = PageRequest.of(page, limit, sort);
        return searchItemsRepository.findAllLikedBands(userUuid, pageable)
                .collectList()
                .zipWith(searchItemsRepository.findTotalLikedBands(userUuid))
                .map(tuple -> {
                    Map<String, List<SearchEntityResponse>> response = new HashMap<>();
                    response.put("liked_bands", tuple.getT1());
                    long total = tuple.getT2();

                    return ResponseEntity.ok()
                            .header("X-Total-Count", String.valueOf(total))
                            .body(response);
                });
    }

    @GetMapping("/artists/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public Mono<ResponseEntity<Map<String, List<SearchEntityResponse>>>>
    findByTitle(@RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "16") int limit,
                @RequestParam(value = "q") String query,
                @RequestParam String userUuid) {

        Pageable pageable = PageRequest.of(page, limit);
        return searchItemsRepository.findAllLikedBandsByTitle(query, userUuid, pageable)
                .collectList()
                .zipWith(searchItemsRepository.findTotalLikedBandsByTitle(query, userUuid))
                .map(tuple -> {
                    Map<String, List<SearchEntityResponse>> response = new HashMap<>();
                    response.put("found_bands", tuple.getT1());
                    long total = tuple.getT2();

                    return ResponseEntity.ok()
                            .header("X-Total-Count", String.valueOf(total))
                            .body(response);
                });
    }

}

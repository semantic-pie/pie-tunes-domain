package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.repositories.neo4j.MusicBandRepository;
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



@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api/v1/library/artists")
public class BandController {

    private final MusicBandRepository musicBandRepository;

    @Deprecated
    @GetMapping()
    public Flux<MusicBand> getMethodName(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "8") int limit) {

        return musicBandRepository.findAllBands().skip(page).take(limit);
    }


    @GetMapping("/{uuid}")
    @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Band uuid")
    public ResponseEntity<Mono<MusicBand>>
    findTrackByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok()
                .body(musicBandRepository.findMusicBandByUuid(uuid));
    }

    @GetMapping("/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name = "order", schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public ResponseEntity<Flux<MusicBand>>
    findArtistsByDate(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "16") int limit,
                      @RequestParam(defaultValue = "desc") String order,
                      @RequestParam String userUuid) {

        Sort sort = Sort.by(Sort.Direction.fromString(order.toLowerCase()), "r.createdAt");
        Pageable pageable = PageRequest.of(page, limit, sort);

        Mono<Long> totalLikedBands =
                musicBandRepository.findTotalLikedBands(userUuid);

        Flux<MusicBand> allLikedBands =
                musicBandRepository.findAllLikedBands(userUuid, pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalLikedBands.block()))
                .body(allLikedBands);
    }

    @GetMapping("/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name = "q", schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name = "limit", schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    @Parameter(in = ParameterIn.QUERY, name = "userUuid", schema = @Schema(type = "string"))
    // This user-cringe uuid parameter will be deleted after security implementation
    public ResponseEntity<Flux<MusicBand>>
    findByTitle(@RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "16") int limit,
                @RequestParam(value = "q") String query,
                @RequestParam String userUuid) {

        Pageable pageable = PageRequest.of(page, limit);

        Mono<Long> totalLikedBandsByTitle =
                musicBandRepository.findTotalLikedBandsByTitle(query.toLowerCase(), userUuid);

        Flux<MusicBand> allLikedBandsByTitle =
                musicBandRepository.findAllLikedBandsByTitle(query.toLowerCase(), userUuid, pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalLikedBandsByTitle.block()))
                .body(allLikedBandsByTitle);
    }

}

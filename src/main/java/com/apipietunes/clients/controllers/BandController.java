package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.repositories.neo4j.MusicBandRepository;
import com.apipietunes.clients.repositories.neo4j.TrackMetadataRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api")
public class BandController {

    private final MusicBandRepository musicBandRepository;

    @Deprecated
    @GetMapping("/artists")
    public Flux<MusicBand> getMethodName(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "8") int limit) {

        return musicBandRepository.findAll().skip(page).take(limit);
    }

    @GetMapping("/artists/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name ="order" ,schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name ="page" ,schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name ="limit" ,schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public Flux<MusicBand> findByDate(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "16") int limit,
                                         @RequestParam(defaultValue = "16") int order) {
        return null;
    }

    @GetMapping("/artists/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name ="query" ,schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name ="page" ,schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name ="limit" ,schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public Flux<MusicBand> findByTitle(@RequestParam(defaultValue = "0") long page,
                                        @RequestParam(defaultValue = "16") long limit,
                                        @RequestParam() String query) {
        return null;
    }

}

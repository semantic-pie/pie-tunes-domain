package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.repositories.neo4j.TrackMetadataRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
@RequestMapping("/api")
public class TrackController {

    private final TrackMetadataRepository trackMetadataRepository;

    @Deprecated
    @GetMapping("/tracks")
    public Flux<MusicTrack> getMethodName(@RequestParam(defaultValue = "0") long page,
                                          @RequestParam(defaultValue = "8") long limit) {
        return trackMetadataRepository.findAll().skip(page * limit).take(limit);
    }

    @GetMapping("/tracks/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name ="order" ,schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name ="page" ,schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name ="limit" ,schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public Flux<MusicTrack> findByDate(@RequestParam(defaultValue = "0") long page,
                                          @RequestParam(defaultValue = "16") long limit,
                                          @RequestParam(defaultValue = "asc") String order) {
        return null;
    }

    @GetMapping("/tracks/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name ="query" ,schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name ="page" ,schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name ="limit" ,schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public Flux<MusicTrack> findByTitle(@RequestParam(defaultValue = "0") long page,
                                          @RequestParam(defaultValue = "16") long limit,
                                          @RequestParam String query) {
       return null;
    }

    @GetMapping("/tracks/find-by-album/{uuid}")
    @Operation(description = "Find all tracks of album with uuid.")
    @Parameter(in = ParameterIn.PATH, name ="uuid", description = "Album uuid")
    public Flux<MusicTrack> findByAlbum(@PathVariable String uuid) {
        // if possible, tracks without album nested field (for optimization)
        return null;
    }
}

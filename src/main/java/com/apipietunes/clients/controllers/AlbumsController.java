package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.repositories.neo4j.MusicAlbumRepository;
import com.apipietunes.clients.repositories.neo4j.MusicBandRepository;
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
public class AlbumsController {

    private final MusicAlbumRepository musicAlbumRepository;

    @Deprecated
    @GetMapping("/albums")
    public Flux<MusicAlbum> getMethodName(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "8") int limit) {

        return musicAlbumRepository.findAll().skip(page).take(limit);
    }

    @GetMapping("/albums/find-by-date")
    @Parameter(in = ParameterIn.QUERY, name ="order" ,schema = @Schema(type = "string", allowableValues = {"asc", "desc"}))
    @Parameter(in = ParameterIn.QUERY, name ="page" ,schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name ="limit" ,schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public Flux<MusicAlbum> findByDate(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "16") int limit,
                                      @RequestParam(defaultValue = "16") int order) {
        return null;
    }

    @GetMapping("/albums/find-by-title")
    @Parameter(in = ParameterIn.QUERY, name ="query" ,schema = @Schema(type = "string", minLength = 1, maxLength = 20))
    @Parameter(in = ParameterIn.QUERY, name ="page" ,schema = @Schema(type = "integer", minimum = "0"))
    @Parameter(in = ParameterIn.QUERY, name ="limit" ,schema = @Schema(type = "integer", minimum = "1", maximum = "100"))
    public Flux<MusicAlbum> findByTitle(@RequestParam(defaultValue = "0") long page,
                                        @RequestParam(defaultValue = "16") long limit,
                                        @RequestParam(required = false) String query) {
        return null;
    }
}

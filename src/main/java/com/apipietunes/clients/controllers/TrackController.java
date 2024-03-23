package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.repositories.neo4j.TrackMetadataRepository;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class TrackController {

    private final TrackMetadataRepository trackMetadataRepository;

    @GetMapping("/api/tracks")
    public Flux<MusicTrack> getMethodName(@RequestParam(defaultValue = "0") long page,
                                          @RequestParam(defaultValue = "8") long limit) {

        return trackMetadataRepository.findAll().skip(page * limit).take(limit);
    }

}

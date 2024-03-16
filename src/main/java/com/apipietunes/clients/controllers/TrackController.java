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
    public Flux<MusicTrack> getMethodName() {
        return trackMetadataRepository.findAll();
    }

    /*@GetMapping("/api/tracks/search")
    public Flux<Trackdto> test(@RequestParam(required = false, value = "q") String searchQuery,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "8") int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Flux<Trackdto> map = curwa.findAllMatches(searchQuery, pageable);
        System.out.println("qqwert");
        return map;
    }*/
    
}

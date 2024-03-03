package com.apipietunes.clients.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;
import com.apipietunes.clients.repositories.TrackMetadatRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class Trackontroller {
    private final TrackMetadatRepository trackMetadatRepository;

    @GetMapping("/api/tracks")
    public Flux<MusicTrack> getMethodName() {
        return trackMetadatRepository.findAll();
    }
    
}

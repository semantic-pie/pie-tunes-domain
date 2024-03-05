package com.apipietunes.clients.controllers;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.apipietunes.clients.services.TrackStreamingService;
import com.apipietunes.clients.services.exceptions.TrackNotFoundException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrackTestStreamer {

    private final TrackStreamingService trackStreamingService;

    @GetMapping("/api/play/{id}.mp3")
    public Mono<ResponseEntity<InputStreamResource>> play(@PathVariable String id) {
        return trackStreamingService.getTrackById(id).map(inputStream -> ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .body(new InputStreamResource(inputStream)))
                .switchIfEmpty(Mono.error(new TrackNotFoundException("Track with id '" + id + "' not found.")));
    }

    @GetMapping("/api/tracks/covers/{id}")
    public Mono<ResponseEntity<InputStreamResource>> cover(@PathVariable String id) {
        return trackStreamingService.getTrackCoverById(id).map(inputStream -> ResponseEntity.ok()
                .contentType(MediaType.valueOf(inputStream.headers().get("Content-Type")))
                .body(new InputStreamResource(inputStream)))
                .switchIfEmpty(
                        Mono.error(new TrackNotFoundException("Cover for track with id '" + id + "' not found.")));
    }
}

package com.apipietunes.clients.controllers;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${minio.bucket}")
    public String BUCKET_NAME;

    private final TrackStreamingService trackStreamingService;

    @GetMapping("/api/play/{id}")
    public Mono<ResponseEntity<InputStreamResource>> getMethodName(@PathVariable String id) {
        return trackStreamingService.getById(id).map(inputStream -> ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .body(new InputStreamResource(inputStream)))
                .switchIfEmpty(Mono.error(new TrackNotFoundException("Track with id '" + id + "' not found.")));
    }
}

package com.apipietunes.clients.controllers;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.apipietunes.clients.services.TrackLoaderService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TrackTestStreamer {

    private final TrackLoaderService trackLoaderService;

    @GetMapping("/api/play/{id}")
    public Mono<ResponseEntity<?>> getMethodName(@PathVariable String id) {
            var resource = trackLoaderService.load(id);
        
            return resource.map(inputStream -> {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.valueOf("audio/mpeg"));

                return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inputStream));
            });
    }

}

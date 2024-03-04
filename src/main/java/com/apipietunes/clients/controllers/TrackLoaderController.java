package com.apipietunes.clients.controllers;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.apipietunes.clients.services.TrackLoaderService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TrackLoaderController {

  private final TrackLoaderService trackLoaderService;

  @PostMapping(value = "/api/track-loader/upload", consumes = "multipart/form-data")
  public Mono<String> handleFileUpload(@RequestPart("file") Flux<FilePart> filePartFlux) {
    return filePartFlux.collectList()
        .flatMap(trackLoaderService::saveAll)
        .then(Mono.just("Uploaded"));
  }
}

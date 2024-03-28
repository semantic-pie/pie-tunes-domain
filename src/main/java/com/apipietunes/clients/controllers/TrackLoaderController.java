package com.apipietunes.clients.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;

import com.apipietunes.clients.services.TrackLoaderService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class TrackLoaderController {

  private final TrackLoaderService trackLoaderService;

  @Operation(description = "Upload multiple multipart files. (overflow is possible)")
  @PostMapping(value = "/track-loader/upload", consumes = "multipart/form-data")
  public Mono<String> handleFileUpload(@RequestPart("file") Flux<FilePart> filePartFlux) {
    return filePartFlux.collectList()
        .flatMap(trackLoaderService::saveAll)
        .then(Mono.just("Uploaded"));
  }

  @Operation(description = "Upload single multipart file.")
  @PostMapping(value = "/track-loader/upload-one", consumes = "multipart/form-data")
  public Mono<String> handleFileUpload(@RequestPart("file") Mono<FilePart> filePartFlux) {
    return filePartFlux.flatMap(trackLoaderService::save).then(Mono.just("Uploaded"));
  }
}

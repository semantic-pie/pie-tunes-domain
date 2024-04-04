package com.apipietunes.clients.controllers;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;

import com.apipietunes.clients.models.dtos.TrackLoaderResponseDto;
import com.apipietunes.clients.services.TrackLoaderService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class TrackLoaderController {

  private final TrackLoaderService trackLoaderService;

  @ResponseStatus(HttpStatus.CREATED)
  @Operation(description = "Upload multiple multipart files. (overflow is possible)")
  @PostMapping(value = "/track-loader/upload", consumes = "multipart/form-data")
  public Mono<Void> handleFilesUpload(@RequestPart("file") Flux<FilePart> filePartFlux) {
    return filePartFlux.collectList()
        .flatMap(trackLoaderService::saveAll)
        .then();
  }

  @ResponseStatus(HttpStatus.CREATED)
  @Operation(description = "Upload single multipart file.")
  @PostMapping(value = "/track-loader/upload-one", consumes = "multipart/form-data")
  public Mono<TrackLoaderResponseDto> handleFileUpload(@RequestPart("file") Mono<FilePart> filePartFlux) {
    return filePartFlux.flatMap(trackLoaderService::save).map(TrackLoaderResponseDto::new);
  }
}

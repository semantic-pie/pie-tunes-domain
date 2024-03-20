package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.neo4jDomain.MusicAlbum;
import com.apipietunes.clients.models.neo4jDomain.MusicBand;
import com.apipietunes.clients.repositories.neo4j.MusicAlbumRepository;
import com.apipietunes.clients.repositories.neo4j.MusicBandRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@Slf4j
@RestController
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AlbumsController {

    private final MusicAlbumRepository musicAlbumRepository;

    @GetMapping("/api/albums")
    public Flux<MusicAlbum> getMethodName(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "8") int limit) {

        return musicAlbumRepository.findAll().skip(page).take(limit);
    }

}

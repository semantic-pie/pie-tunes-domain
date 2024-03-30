package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.dtos.ActionEventDto;
import com.apipietunes.clients.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@RestController
@Slf4j
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ActionEventController {

    private final UserService userService;

    @PostMapping("/api/tracks/events")
    public Mono<Void> likeTrack(@RequestBody ActionEventDto event, ServerWebExchange exchange) {

        switch (event.getType()) {
            case LIKE_TRACK -> {
                return userService.likeTrackEvent(event.getTrackUuid(), event.getUserUuid(), exchange);
            }
            case REMOVE_LIKE -> {
                return userService.removeLikeEvent(event.getTrackUuid(), event.getUserUuid(), exchange);
            }
            default -> {
                return Mono.empty();
            }
        }
    }
}

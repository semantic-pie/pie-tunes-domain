package com.apipietunes.clients.controllers;

import com.apipietunes.clients.models.dtos.ActionEventDto;
import com.apipietunes.clients.services.UserService;
import com.apipietunes.clients.services.jwt.JwtTokenProvider;
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
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/api/tracks/events")
    public Mono<Void> likeTrack(@RequestBody ActionEventDto event, ServerWebExchange exchange) {

        String jwtToken = jwtTokenProvider.getJwtTokenFromRequest(exchange.getRequest());
        String userUuid = jwtTokenProvider.getUUID(jwtToken);

        switch (event.getType()) {
            case LIKE_ENTITY -> {
                return userService.likeEntityEvent(event.getEntityUuid(), userUuid);
            }
            case REMOVE_LIKE -> {
                return userService.removeLikeEvent(event.getEntityUuid(), userUuid);
            }
            default -> {
                return Mono.empty();
            }
        }
    }
}

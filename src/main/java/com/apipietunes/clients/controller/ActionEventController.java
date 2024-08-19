package com.apipietunes.clients.controller;

import com.apipietunes.clients.model.dto.ActionEventDto;
import com.apipietunes.clients.service.UserService;
import com.apipietunes.clients.service.jwt.JwtTokenProvider;

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

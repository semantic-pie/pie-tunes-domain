package com.apipietunes.clients.handlers;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import com.apipietunes.clients.models.errors.ApiPieTunesErrorInfo;
import com.apipietunes.clients.services.exceptions.NodeAlreadyExists;
import com.apipietunes.clients.services.exceptions.TrackNotFoundException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrackNotFoundException.class)
    public Mono<ApiPieTunesErrorInfo> handleTrackNotFound(ServerWebExchange exchange, Exception ex) {
        log.warn(ex.getMessage());
        return Mono.just(new ApiPieTunesErrorInfo(HttpStatus.NOT_FOUND.value(),
                exchange.getRequest().getPath().toString(), ex.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(NodeAlreadyExists.class)
    public Mono<ApiPieTunesErrorInfo> handleConflicts(ServerWebExchange exchange, Exception ex) {
        log.warn(ex.getMessage());
        return Mono.just(new ApiPieTunesErrorInfo(HttpStatus.CONFLICT.value(),
                exchange.getRequest().getPath().toString(), ex.getMessage()));
    }
}
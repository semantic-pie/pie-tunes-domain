package com.apipietunes.clients.models.messages;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ApiPieTunesMessageInfo {
    private LocalDateTime timestamp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private String url;
    private int status;
    private String message;

    private ApiPieTunesMessageInfo() {
        timestamp = LocalDateTime.now();
    }

    public ApiPieTunesMessageInfo(int status, String url, String message) {
        this();
        this.status = status;
        this.url = url;
        this.message = message;

    }
    // Egor: в дальнейшем можно будет добавить список subErrors
}
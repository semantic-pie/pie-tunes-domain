package com.apipietunes.clients.model.error;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ApiPieTunesErrorInfo {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String url;
    private int status;
    private String message;

    private ApiPieTunesErrorInfo() {
        timestamp = LocalDateTime.now();
    }

    public ApiPieTunesErrorInfo(int status, String url, String message) {
        this();
        this.status = status;
        this.url = url;
        this.message = message;

    }
    // Egor: в дальнейшем можно будет добавить список subErrors
}
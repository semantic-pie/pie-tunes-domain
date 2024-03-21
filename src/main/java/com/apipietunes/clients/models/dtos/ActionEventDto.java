package com.apipietunes.clients.models.dtos;

import com.apipietunes.clients.models.enums.ActionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActionEventDto {

    private ActionType type;

    @JsonProperty("track_uuid")
    private String trackUuid;

    @JsonProperty("user_uuid")
    private String userUuid;
}

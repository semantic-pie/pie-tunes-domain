package com.apipietunes.clients.models.dtos;

import com.apipietunes.clients.models.enums.ActionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActionEventDto {

    private ActionType type;

    private String trackUuid;

}

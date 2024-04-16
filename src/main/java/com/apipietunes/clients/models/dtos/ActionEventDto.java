package com.apipietunes.clients.models.dtos;

import com.apipietunes.clients.models.enums.ActionType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActionEventDto {

    private ActionType type;

    private String entityUuid;

}

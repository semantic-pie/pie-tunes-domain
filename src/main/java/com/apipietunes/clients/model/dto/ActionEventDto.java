package com.apipietunes.clients.model.dto;

import com.apipietunes.clients.model.enums.ActionType;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActionEventDto {

    private ActionType type;

    private String entityUuid;

}

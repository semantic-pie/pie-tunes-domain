package com.apipietunes.clients.util;

import com.apipietunes.clients.model.entity.MusicTrack;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TrackMetadataParseResult {
    private final MusicTrack musicTrack;
    private final byte[] cover;
    private final String coverMimeType;
}

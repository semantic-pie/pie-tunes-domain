package com.apipietunes.clients.utils;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;

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

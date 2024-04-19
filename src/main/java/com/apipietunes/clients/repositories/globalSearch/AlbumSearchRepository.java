package com.apipietunes.clients.repositories.globalSearch;

import com.apipietunes.clients.models.dtos.domain.MusicAlbumDto;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface AlbumSearchRepository extends ReactiveNeo4jRepository<MusicAlbumDto, UUID> {

    @Query("""
            MATCH (musicAlbumDto:Album)
            WHERE toLower(musicAlbumDto.name) CONTAINS toLower($searchQuery)
            OPTIONAL MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(musicAlbumDto:Album)
            MATCH (musicAlbumDto)<-[has_album:HAS_ALBUM]-(musicAlbumDto_musicBand:Band)
            WITH musicAlbumDto, collect(has_album) as has_album, collect(musicAlbumDto_musicBand) as musicAlbumDto_musicBand,  COUNT(r) AS isLiked
            RETURN musicAlbumDto{
                .uuid,
                .name,
                .description,
                .yearOfRecord,
                isLiked: CASE WHEN isLiked > 0 THEN true ELSE false END
            }, has_album, musicAlbumDto_musicBand
            LIMIT 4
            """)
    Flux<MusicAlbumDto> findAllByName(String userUuid, String searchQuery);
}
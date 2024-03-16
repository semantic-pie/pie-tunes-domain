package com.apipietunes.clients.repositories.neo4j;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import com.apipietunes.clients.models.neo4jDomain.MusicTrack;

import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface TrackMetadataRepository extends ReactiveNeo4jRepository<MusicTrack, UUID> {
    Mono<MusicTrack> findByTitleAndMusicBand_Name(String title, String musicBand_Name);
    Mono<MusicTrack> findByUuid(UUID uuid);

//    @Query("MERGE (b:Band {name: :#{#musicBand.name}}) ON CREATE SET g.version = 0 RETURN g")
//@Query("MATCH (a:Album {name: :#{#musicAlbum.name}}) <- [r:HAS_ALBUM] - (b:Band {name: :#{#musicAlbum.musicBand.name}}) RETURN a")
    @Query("MERGE (t:Track {title: :#{#musicTrack.title}}) <- [r:HAS_TRACK] - (b:Band {name: :#{#musicTrack.musicBand.name}}) " +
            "ON CREATE S")
    Mono<MusicTrack> persist(MusicTrack musicTrack);

    /*@Query("MATCH (t:Track) " +
            "WHERE toLower(t.title) CONTAINS toLower($searchQuery) " +
            "RETURN t.uuid AS uuid, " +
            "t.version AS version, " +
            "t.title AS title, " +
            "t.releaseYear AS releaseYear, " +
            "t.bitrate AS bitrate, " +
            "t.lengthInMilliseconds AS lengthInMilliseconds, " +
            "t.playlist AS playlist, " +
            "t.genres AS genres, " +
            "t.musicBand AS musicBand, " +
            "t.musicAlbum AS musicAlbum " +
            "ORDER BY t.title " +
            "SKIP :#{#pageable.getPageNumber()} " +
            "LIMIT :#{#pageable.getPageSize()}")*/
    /*@Query("MATCH (a:Album) - [r1:CONTAINS] -> (t:Track) <- [r2:HAS_TRACK] - (b:Band)" +
            "WHERE toLower(t.title) CONTAINS toLower($searchQuery) " +
            "RETURN t.uuid AS uuid, " +
            "t.title AS title, " +
            "t.bitrate AS bitrate, " +
            "t.lengthInMilliseconds AS lengthInMilliseconds, " +
            "COLLECT(DISTINCT b.name) AS musicBand, " +
            "COLLECT(DISTINCT a.name) AS musicAlbum " +
            "ORDER BY t.title " +
            "SKIP :#{#pageable.getPageNumber()} " +
            "LIMIT :#{#pageable.getPageSize()}")*/
    /*@Query("MATCH (a:Album)-[r1:CONTAINS]->(t:Track)<-[r2:HAS_TRACK]-(b:Band)\n" +
            "WHERE toLower(t.title) CONTAINS toLower($searchQuery)\n" +
            "RETURN t.uuid AS uuid,\n" +
            "       t.title AS title,\n" +
            "       t.bitrate AS bitrate,\n" +
            "       t.lengthInMilliseconds AS lengthInMilliseconds,\n" +
            "       COLLECT(DISTINCT b.name) AS musicBand,\n" +
            "       COLLECT(DISTINCT a.name) AS musicAlbum\n" +
            "ORDER BY t.title\n" +
            "SKIP :#{#pageable.getPageNumber()}\n" +
            "LIMIT :#{#pageable.getPageSize()}")
    Flux<Trackdto> findAllMatches(String searchQuery, Pageable pageable);*/


}

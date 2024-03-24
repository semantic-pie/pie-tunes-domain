package com.apipietunes.clients.repositories.neo4j;

import com.apipietunes.clients.models.dtos.SearchEntityResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SearchItemsRepository extends ReactiveNeo4jRepository<SearchEntityResponse, UUID> {

    /*@Query("""
        MATCH (a:Album) <- [:HAS_ALBUM] - (b:Band)
        WHERE toLower(a.name) CONTAINS toLower($searchQuery)
        WITH COUNT(a) AS total

        MATCH (a:Album) <- [:HAS_ALBUM] - (b:Band)
        WHERE toLower(a.name) CONTAINS toLower($searchQuery)
        RETURN  a.uuid AS uuid,
                'ALBUM' AS entity_type,
                a.name AS name,
                b.name AS band_name,
                total
        ORDER BY a.name
        SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
        LIMIT :#{#pageable.getPageSize()}

        UNION
        MATCH (b1:Band)
        WHERE toLower(b1.name) CONTAINS toLower($searchQuery)
        WITH collect(b1) AS b1List, count(b1) AS total
        UNWIND b1List AS b1
        RETURN  b1.uuid AS uuid,
                'BAND' AS entity_type,
                b1.name AS name,
                b1.name AS band_name,
                total
        ORDER BY b1.name
        SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
        LIMIT :#{#pageable.getPageSize()}

        UNION
        MATCH (t:Track) <- [:HAS_TRACK] - (b2:Band)
        WHERE toLower(t.title) CONTAINS toLower($searchQuery)
        WITH COUNT(t) AS total

        MATCH (t:Track) <- [:HAS_TRACK] - (b2:Band)
        WHERE toLower(t.title) CONTAINS toLower($searchQuery)
        RETURN  t.uuid AS uuid,
                'TRACK' AS entity_type,
                t.title AS name,
                b2.name AS band_name,
                total
        ORDER BY t.title
        SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
        LIMIT :#{#pageable.getPageSize()}
        """)
    Flux<SearchEntityResponse> findAllMatches(String searchQuery, Pageable pageable);*/

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(t:Track)
            WITH COUNT(t) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedTracks(String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(t:Track) <- [:HAS_TRACK] - (b:Band)
            RETURN  t.uuid AS uuid,
                    'TRACK' AS entity_type,
                    t.title AS name,
                    b.name AS band_name
            :#{orderBy(#pageable)}
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<SearchEntityResponse> findAllLikedTracks(String userUuid, Pageable pageable);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(t:Track)
            WHERE toLower(t.title) CONTAINS toLower($searchQuery)
            WITH COUNT(t) AS total
            RETURN total
            """)
    Mono<Long> findTotalLikedTracksByTitle(String searchQuery, String userUuid);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(t:Track) <- [:HAS_TRACK] - (b:Band)
            WHERE toLower(t.title) CONTAINS toLower($searchQuery)
            RETURN  t.uuid AS uuid,
                    'TRACK' AS entity_type,
                    t.title AS name,
                    b.name AS band_name
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<SearchEntityResponse> findAllLikedTracksByTitle(String searchQuery, String userUuid, Pageable pageable);

    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(a:Album)
            WITH COUNT(a) AS total
                    
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(a:Album) <- [:HAS_ALBUM] - (b:Band)
            RETURN  a.uuid AS uuid,
                    'ALBUM' AS entity_type,
                    a.name AS name,
                    b.name AS band_name,
                    total
            :#{orderBy(#pageable)}
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<SearchEntityResponse> findAllLikedAlbums(String userUuid, Pageable pageable);


    @Query("""
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(b:Band)
            WITH COUNT(b) AS total
                    
            MATCH (u:User {uuid: $userUuid})-[r:LIKES]->(b:Band)
            RETURN  b.uuid AS uuid,
                    'BAND' AS entity_type,
                    b.name AS name,
                    b.name AS band_name,
                    total
            :#{orderBy(#pageable)}
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<SearchEntityResponse> findAllLikedBands(String userUuid, Pageable pageable);


}

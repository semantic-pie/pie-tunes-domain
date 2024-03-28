package com.apipietunes.clients.repositories.neo4j;

import com.apipietunes.clients.models.dtos.SearchEntityResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SearchItemsRepository extends ReactiveNeo4jRepository<SearchEntityResponse, UUID> {

    @Query("""
            MATCH (a:Album)<-[:HAS_ALBUM]-(b:Band)
            WHERE toLower(a.name) CONTAINS toLower($searchQuery)
            WITH COUNT(a) AS total
            RETURN total
            """)
    Mono<Long> findTotalAllMatchedAlbums(String searchQuery);

    @Query("""
            MATCH (b:Band)
            WHERE toLower(b.name) CONTAINS toLower($searchQuery)
            WITH COUNT(b) AS total
            RETURN total
            """)
    Mono<Long> findTotalAllMatchedBands(String searchQuery);

    @Query("""
            MATCH (t:Track)<-[:HAS_TRACK]-(b:Band)
            WHERE toLower(t.title) CONTAINS toLower($searchQuery)
            WITH COUNT(t) AS total
            RETURN total
            """)
    Mono<Long> findTotalAllMatchedTracks(String searchQuery);

    @Query("""
            MATCH (a:Album)<-[:HAS_ALBUM]-(b:Band)
            WHERE toLower(a.name) CONTAINS toLower($searchQuery)
            RETURN  a.uuid AS uuid,
                    'ALBUM' AS entity_type,
                    a.name AS name,
                    b.name AS band_name
            ORDER BY a.name
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}

            UNION
            MATCH (b1:Band)
            WHERE toLower(b1.name) CONTAINS toLower($searchQuery)
            RETURN  b1.uuid AS uuid,
                    'BAND' AS entity_type,
                    b1.name AS name,
                    b1.name AS band_name
            ORDER BY b1.name
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}

            UNION
            MATCH (t:Track)<-[:HAS_TRACK]-(b2:Band)
            WHERE toLower(t.title) CONTAINS toLower($searchQuery)
            RETURN  t.uuid AS uuid,
                    'TRACK' AS entity_type,
                    t.title AS name,
                    b2.name AS band_name
            ORDER BY t.title
            SKIP :#{#pageable.getPageNumber()}*:#{#pageable.getPageSize()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<SearchEntityResponse> findAllMatches(String searchQuery, Pageable pageable);


}

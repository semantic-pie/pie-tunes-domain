package com.apipietunes.clients.repositories.neo4j;

import com.apipietunes.clients.models.dtos.SearchEntityResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SearchItemsRepository extends ReactiveNeo4jRepository<SearchEntityResponse, UUID> {

    @Query("""
            MATCH (a:Album) <- [r:HAS_ALBUM] - (b:Band)
            WHERE toLower(a.name) CONTAINS toLower($searchQuery)
            RETURN  a.uuid AS uuid,
                    'ALBUM' AS entity_type,
                    a.name AS name,
                    b.name AS band_name
            ORDER BY a.name
            SKIP :#{#pageable.getPageNumber()}
            LIMIT :#{#pageable.getPageSize()}
                        
            UNION
            MATCH (b1:Band)
            WHERE toLower(b1.name) CONTAINS toLower($searchQuery)
            RETURN  b1.uuid AS uuid,
                    'BAND' AS entity_type,
                    b1.name AS name,
                    b1.name AS band_name
            ORDER BY b1.name
            SKIP :#{#pageable.getPageNumber()}
            LIMIT :#{#pageable.getPageSize()}
                        
            UNION
            MATCH (t:Track) <- [r1:HAS_TRACK] - (b2:Band)
            WHERE toLower(t.title) CONTAINS toLower($searchQuery)
            RETURN  t.uuid AS uuid,
                    'TRACK' AS entity_type,
                    t.title AS name,
                    b2.name AS band_name
            ORDER BY t.title
            SKIP :#{#pageable.getPageNumber()}
            LIMIT :#{#pageable.getPageSize()}
            """)
    Flux<SearchEntityResponse> findAllMatches(String searchQuery, Pageable pageable);


}

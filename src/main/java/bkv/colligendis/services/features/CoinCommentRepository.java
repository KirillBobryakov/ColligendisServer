package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.item.CoinComment;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

public interface CoinCommentRepository extends AbstractNeo4jRepository<CoinComment> {



    @Query("MATCH (n) WHERE id(n)=$id SET n.$property = '$value' RETURN n")
    Mono<CoinComment> setPropertyValue(Long id, String property, String value);

}

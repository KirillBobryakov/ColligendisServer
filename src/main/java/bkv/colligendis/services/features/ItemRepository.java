package bkv.colligendis.services.features;


import bkv.colligendis.database.entity.item.CoinComment;
import bkv.colligendis.database.entity.item.Item;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

public interface ItemRepository extends AbstractNeo4jRepository<Item> {


    Mono<Item> findByNumistaNumber(String numistaNumber);


    @Query("MATCH (c)-[]->(s) WHERE id(s)=$pieceSideId RETURN c")
    Mono<Item> findByPieceSideId(long pieceSideId);


    Mono<Boolean> existsByNumistaNumber(String numistaNumber);

    @Query("MATCH (n) WHERE id(n)=$id SET n.$property = '$value' RETURN n")
    Mono<Item> setPropertyValue(Long id, String property, String value);


}

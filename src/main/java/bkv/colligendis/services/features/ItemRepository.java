package bkv.colligendis.services.features;


import bkv.colligendis.database.entity.piece.Item;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

public interface ItemRepository extends ReactiveNeo4jRepository<Item, Long> {


    Mono<Item> findByNumistaNumber(String numistaNumber);


    @Query("MATCH (c)-[]->(s) WHERE id(s)=$pieceSideId RETURN c")
    Mono<Item> findByPieceSideId(long pieceSideId);


}

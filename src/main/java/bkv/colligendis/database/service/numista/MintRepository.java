package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.database.entity.numista.Shape;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MintRepository extends AbstractNeo4jRepository<Mint> {

    Mono<Mint> findByNid(String nid);
    Mono<Mint> findByNumistaURL(String numistaURL);
    Mono<Mint> findByName(String name);

    Flux<Mint> findByNameContainingIgnoreCase(String nameFilter);

    @Query("MATCH (m:MINT)-[r]-() WHERE id(m)=$mintId RETURN count(r)")
    Mono<Integer> countRelations(String mintId);

}

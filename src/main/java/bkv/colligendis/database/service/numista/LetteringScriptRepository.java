package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.LetteringScript;
import bkv.colligendis.database.entity.numista.Shape;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface LetteringScriptRepository extends AbstractNeo4jRepository<LetteringScript> {

    Mono<LetteringScript> findByNid(String nid);
}

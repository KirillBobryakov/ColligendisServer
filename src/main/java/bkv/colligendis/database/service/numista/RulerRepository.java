package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface RulerRepository extends AbstractNeo4jRepository<Ruler> {

    Mono<Ruler> findByNid(String value);
}

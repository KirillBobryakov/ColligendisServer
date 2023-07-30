package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Value;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface ValueRepository extends AbstractNeo4jRepository<Value> {

    Mono<Value> findByName(String name);
}

package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Value;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface ValueRepository extends ReactiveNeo4jRepository<Value, Long> {

    Mono<Value> findByName(String name);
}

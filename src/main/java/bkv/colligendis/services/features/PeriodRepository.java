package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Period;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface PeriodRepository extends AbstractNeo4jRepository<Period> {

    Mono<Period> findByName(String name);
}

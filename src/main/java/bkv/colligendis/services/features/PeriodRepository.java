package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Period;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface PeriodRepository extends ReactiveNeo4jRepository<Period, Long> {

    Mono<Period> findByName(String name);
}

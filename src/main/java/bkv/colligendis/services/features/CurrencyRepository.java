package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Currency;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface CurrencyRepository extends ReactiveNeo4jRepository<Currency, Long> {

    Mono<Currency> findByName(String name);
}

package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface CurrencyRepository extends AbstractNeo4jRepository<Currency> {

    Mono<Currency> findByNid(String nid);
    Mono<Currency> findByFullName(String fullName);
    Mono<Currency> findByName(String name);
}

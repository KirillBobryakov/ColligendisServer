package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.features.Territory;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface TerritoryRepository extends AbstractNeo4jRepository<Territory> {


    Mono<Territory> findByName(String name);

}

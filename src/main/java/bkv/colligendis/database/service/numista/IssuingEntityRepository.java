package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.IssuingEntity;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface IssuingEntityRepository extends AbstractNeo4jRepository<IssuingEntity> {

    Mono<IssuingEntity> findByName(String name);
}

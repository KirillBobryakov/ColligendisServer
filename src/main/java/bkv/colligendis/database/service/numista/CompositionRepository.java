package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Composition;
import bkv.colligendis.database.entity.numista.Metal;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface CompositionRepository extends AbstractNeo4jRepository<Composition> {
}

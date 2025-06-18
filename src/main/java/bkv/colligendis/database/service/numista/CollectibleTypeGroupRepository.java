package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CollectibleTypeGroup;
import bkv.colligendis.database.entity.numista.Type;
import bkv.colligendis.database.entity.numista.TypeGroup;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface CollectibleTypeGroupRepository extends AbstractNeo4jRepository<CollectibleTypeGroup> {

    CollectibleTypeGroup findByName(String name);
}

package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.NTag;
import bkv.colligendis.database.entity.numista.Printer;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface NTagRepository extends AbstractNeo4jRepository<NTag> {

    Mono<NTag> findByNid(String nid);
}

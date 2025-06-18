package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Catalogue;
import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface CatalogueRepository extends AbstractNeo4jRepository<Catalogue> {

    Catalogue findByNid(String nid);
}

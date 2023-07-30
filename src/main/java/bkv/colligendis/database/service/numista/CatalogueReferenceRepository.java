package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Catalogue;
import bkv.colligendis.database.entity.numista.CatalogueReference;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface CatalogueReferenceRepository extends AbstractNeo4jRepository<CatalogueReference> {

    Mono<CatalogueReference> findByNumberAndCatalogue_Nid(String number, String nid);
}

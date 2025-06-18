package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Catalogue;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface CatalogueRepository extends AbstractNeo4jRepository<Catalogue> {

    Catalogue findByNid(String nid);
}

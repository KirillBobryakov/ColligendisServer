package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CatalogueReference;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface CatalogueReferenceRepository extends AbstractNeo4jRepository<CatalogueReference> {

    CatalogueReference findByNumberAndCatalogue_Nid(String number, String nid);
}

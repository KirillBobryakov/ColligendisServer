package bkv.colligendis.database.service.numista;

import org.springframework.data.neo4j.repository.query.Query;

import bkv.colligendis.database.entity.numista.CatalogueReference;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface CatalogueReferenceRepository extends AbstractNeo4jRepository<CatalogueReference> {

    CatalogueReference findByNumberAndCatalogue_Nid(String number, String nid);

    @Query("MATCH (n:CATALOGUE_REFERENCE)-[:REFERENCE_FROM]->(c:CATALOGUE) WHERE n.number = $numberCatalogueReference AND c.nid = $nidCatalogue RETURN n.uuid")
    String findUuidByNumberAndCatalogueNid(String numberCatalogueReference, String nidCatalogue);
}

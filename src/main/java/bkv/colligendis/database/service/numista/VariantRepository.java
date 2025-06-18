package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Variant;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;


public interface VariantRepository extends AbstractNeo4jRepository<Variant> {

    Variant findByNid(String nid);


    @Query("MATCH (i:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT) " +
            "WHERE v.gregorianYear IS NOT NULL " +
            "RETURN DISTINCT v.gregorianYear")
    List<Integer> getYearsOfVariantsByIssuerEid(String eid);

    @Query("MATCH (i:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT) " +
            "WHERE v.minYear IS NOT NULL OR v.maxYear IS NOT NULL " +
            "RETURN DISTINCT v " +
            "ORDER BY v.minYear")
    List<Variant> getBetweenMinMaxYearsOfVariantsByIssuerEid(String eid);
}

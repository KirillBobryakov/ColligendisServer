package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Variant;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface VariantRepository extends AbstractNeo4jRepository<Variant> {

    Mono<Variant> findByNid(String nid);


    @Query("MATCH (i:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT) " +
            "WHERE v.gregorianYear IS NOT NULL " +
            "RETURN DISTINCT v.gregorianYear")
    Flux<Integer> getYearsOfVariantsByIssuerEid(String eid);

    @Query("MATCH (i:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT) " +
            "WHERE v.minYear IS NOT NULL OR v.maxYear IS NOT NULL " +
            "RETURN DISTINCT v " +
            "ORDER BY v.minYear")
    Flux<Variant> getBetweenMinMaxYearsOfVariantsByIssuerEid(String eid);
}

package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NTypeRepository extends AbstractNeo4jRepository<NType> {

    Mono<NType> findByNid(String nid);

    Mono<Boolean> existsByNid(String nid);

    @Query("MATCH (n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name LIMIT 50")
    Flux<NType> findByTitleFilter(String filter);

    @Query("MATCH (c:COUNTRY {eid:$eid})-[*1..5]-(n:NTYPE) RETURN n ORDER BY n.name LIMIT 50")
    Flux<NType> findByCountryEid(String eid);

    @Query("MATCH (c:COUNTRY {eid:$eid})-[*1..5]-(n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name LIMIT 50")
    Flux<NType> findByTitleFilterAndCountryEid(String filter, String eid);

    @Query("MATCH (c:SUBJECT {eid:$eid})-[*1..5]->(:ISSUER)-[:ISSUED_BY]-(n:NTYPE) RETURN n ORDER BY n.name LIMIT 50")
    Flux<NType> findBySubjectEid(String eid);

    @Query("MATCH (c:SUBJECT {eid:$eid})-[*1..5]->(:ISSUER)-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART) " +
            "WHERE (v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
            "RETURN DISTINCT n, collect(ro),collect(o),collect(rr),collect(r), collect(rv), collect(v) ORDER BY n.name LIMIT 50")
    Flux<NType> findBySubjectEidAndYear(String eid, int year);

    @Query("MATCH (c:SUBJECT {eid:$eid})-[*1..5]->(:ISSUER)-[:ISSUED_BY]-(n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name LIMIT 50")
    Flux<NType> findByTitleFilterAndSubjectEid(String filter, String eid);

    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE) RETURN n ORDER BY n.title")
    Flux<NType> findByIssuerEid(String eid);

    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE) RETURN n.nid ORDER BY n.title")
    Flux<String> findNTypeNidByIssuerEid(String eid);

    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART) " +
            "WHERE (v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
            "RETURN DISTINCT n,collect(ro),collect(o),collect(rr),collect(r),collect(rv),collect(v) ORDER BY n.title")
    Flux<NType> findByIssuerEidAndYear(String eid, int year);

//    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
//            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
//            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART) " +
//            "WHERE (v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
//            "WITH DISTINCT n.title as title, count(v) as variantsCount " +
//            "RETURN title,variantsCount ORDER BY title")
//    Flux<MinMaxYears> findByIssuerEidAndYearWithVariantsCount(String eid, int year);

    @Query("MATCH (c:ISSUER {eid:$issuerEid})<-[:ISSUED_BY]-(n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name")
    Flux<NType> findByTitleFilterAndIssuerEid(String filter, String issuerEid);

    @Query("MATCH (c:ISSUER {eid:$issuerEid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART)  " +
            "WHERE n.title =~ $filter AND ((v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
            "RETURN DISTINCT n,ro,o,rr,r ORDER BY n.name")
    Flux<NType> findByTitleFilterAndIssuerEidAndYear(String filter, String issuerEid, int year);



}

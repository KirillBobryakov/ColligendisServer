package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface NTypeRepository extends AbstractNeo4jRepository<NType> {


    NType findByNid(String nid);

    /**
     * Find an NTYPE by NTYPE's UUID and compare unique field NTYPE's {@code title} with parameter {@code title}.
     * @param nTypeUuid NTYPE's UUID
     * @param title compared with parameter
     * @return result of comparing
     */
    @Query("MATCH (n) WHERE n.uuid=$nTypeUuid RETURN n.title = $title")
    Boolean compareTitle(String nTypeUuid, String title);
//
//
//    /**
//     * Find related ISSUER's code by NTYPE's UUID
//     * @param uuid NTYPE's UUID
//     * @return UUID  in String value
//     */
//    @Query("MATCH (n:NTYPE)-[:ISSUED_BY]->(i:ISSUER) WHERE n.uuid=$uuid RETURN i.code")
//    String getNTypeIssuerCode(String uuid);

    Boolean existsByNid(String nid);


    /**
     * Find a NType's uuid by {@code nid}
     * @param nid NType's nid
     * @return NType's uuid if exists, or null
     */
    @Query("MATCH (n:NTYPE) WHERE n.nid = $nid RETURN n.uuid")
    String findNTypeUuidByNid(String nid);

    /**
     * Set NTYPE's isActual in true
     * @param uuid NTYPE's UUID
     */
    @Query("MATCH (n:NTYPE {uuid:$uuid}) SET n.isActual = toBoolean(true)")
    void setActual(String uuid);


    /**
     * Find an Issuer's uuid with relationship to NType
     * (n:NTYPE)-[:ISSUED_BY]->(i:ISSUER)
     * @param nTypeUuid NType's uuid
     * @return String value of Issuer's uuid
     */
    @Query("MATCH (n:NTYPE)-[:ISSUED_BY]->(i:ISSUER) WHERE n.uuid = $nTypeUuid RETURN i.uuid")
    String getNTypeIssuerUuid(String nTypeUuid);

    /**
     * Find all NTYPE's nid where NTYPE's isActual property is {@code isActual}
     * @param isActual - parameter for a searching proper NTYPEs
     * @return List of NTYPE's nid
     */
    @Query("MATCH (n:NTYPE) WHERE n.isActual = toBoolean($isActual) return n.nid")
    List<String> findNTypeNidListByIsActual(Boolean isActual);



//    @Query("MATCH (n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name LIMIT 50")
//    List<NType> findByTitleFilter(String filter);

//    @Query("MATCH (c:COUNTRY {eid:$eid})-[*1..5]-(n:NTYPE) RETURN n ORDER BY n.name LIMIT 50")
//    List<NType> findByCountryEid(String eid);

//    @Query("MATCH (c:COUNTRY {eid:$eid})-[*1..5]-(n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name LIMIT 50")
//    List<NType> findByTitleFilterAndCountryEid(String filter, String eid);

//    @Query("MATCH (c:SUBJECT {eid:$eid})-[*1..5]->(:ISSUER)-[:ISSUED_BY]-(n:NTYPE) RETURN n ORDER BY n.name LIMIT 50")
//    List<NType> findBySubjectEid(String eid);

//    @Query("MATCH (c:SUBJECT {eid:$eid})-[*1..5]->(:ISSUER)-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
//            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
//            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART) " +
//            "WHERE (v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
//            "RETURN DISTINCT n, collect(ro),collect(o),collect(rr),collect(r), collect(rv), collect(v) ORDER BY n.name LIMIT 50")
//    List<NType> findBySubjectEidAndYear(String eid, int year);

//    @Query("MATCH (c:SUBJECT {eid:$eid})-[*1..5]->(:ISSUER)-[:ISSUED_BY]-(n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name LIMIT 50")
//    List<NType> findByTitleFilterAndSubjectEid(String filter, String eid);


    /**
     * Find List of NType's {@code nid} by Issuer's uuid
     * @param uuid Issuer's uuid
     * @return List of NType's nid
     */
    @Query("MATCH (i:ISSUER)<-[:ISSUED_BY]-(n:NTYPE) WHERE i.uuid = $uuid RETURN n.nid ORDER BY n.title")
    List<String> findNTypeNidListByIssuerEid(String uuid);

    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE) RETURN n.nid ORDER BY n.title")
    List<String> findNTypeNidByIssuerEid(String eid);

    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART) " +
            "WHERE (v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
            "RETURN DISTINCT n,collect(ro),collect(o),collect(rr),collect(r),collect(rv),collect(v) ORDER BY n.title")
    List<NType> findByIssuerEidAndYear(String eid, int year);

//    @Query("MATCH (c:ISSUER {eid:$eid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
//            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
//            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART) " +
//            "WHERE (v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
//            "WITH DISTINCT n.title as title, count(v) as variantsCount " +
//            "RETURN title,variantsCount ORDER BY title")
//    Flux<MinMaxYears> findByIssuerEidAndYearWithVariantsCount(String eid, int year);

    @Query("MATCH (c:ISSUER {eid:$issuerEid})<-[:ISSUED_BY]-(n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name")
    List<NType> findByTitleFilterAndIssuerEid(String filter, String issuerEid);

    @Query("MATCH (c:ISSUER {eid:$issuerEid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART)  " +
            "WHERE n.title =~ $filter AND ((v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) " +
            "RETURN DISTINCT n,ro,o,rr,r ORDER BY n.name")
    List<NType> findByTitleFilterAndIssuerEidAndYear(String filter, String issuerEid, int year);





}

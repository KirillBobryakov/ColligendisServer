package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.NType;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface NTypeRepository extends AbstractNeo4jRepository<NType> {

    NType findByNid(String nid);

    /**
     * Find an NTYPE by NTYPE's UUID and compare unique field NTYPE's {@code title}
     * with parameter {@code title}.
     * 
     * @param nTypeUuid NTYPE's UUID
     * @param title     compared with parameter
     * @return result of comparing
     */
    @Query("MATCH (n) WHERE n.uuid=$nTypeUuid RETURN n.title = $title")
    Boolean compareTitle(String nTypeUuid, String title);
    //
    //
    // /**
    // * Find related ISSUER's code by NTYPE's UUID
    // * @param uuid NTYPE's UUID
    // * @return UUID in String value
    // */
    // @Query("MATCH (n:NTYPE)-[:ISSUED_BY]->(i:ISSUER) WHERE n.uuid=$uuid RETURN
    // i.code")
    // String getNTypeIssuerCode(String uuid);

    Boolean existsByNid(String nid);

    /**
     * Find a NType's uuid by {@code nid}
     * 
     * @param nid NType's nid
     * @return NType's uuid if exists, or null
     */
    @Query("MATCH (n:NTYPE) WHERE n.nid = $nid RETURN n.uuid")
    String findNTypeUuidByNid(String nid);

    /**
     * Set NTYPE's isActual in true
     * 
     * @param uuid NTYPE's UUID
     */
    @Query("MATCH (n:NTYPE {uuid:$uuid}) SET n.isActual = toBoolean(true)")
    void setActual(String uuid);

    /**
     * Find an Issuer's uuid with relationship to NType
     * (n:NTYPE)-[:ISSUED_BY]->(i:ISSUER)
     * 
     * @param nTypeUuid NType's uuid
     * @return String value of Issuer's uuid
     */
    @Query("MATCH (n:NTYPE)-[:ISSUED_BY]->(i:ISSUER) WHERE n.uuid = $nTypeUuid RETURN i.uuid")
    String getNTypeIssuerUuid(String nTypeUuid);

    /**
     * Find all NTYPE's nid where NTYPE's isActual property is {@code isActual}
     * 
     * @param isActual - parameter for a searching proper NTYPEs
     * @return List of NTYPE's nid
     */
    @Query("MATCH (n:NTYPE) WHERE n.isActual = toBoolean($isActual) return n.nid")
    List<String> findNTypeNidListByIsActual(Boolean isActual);

    /**
     * Find List of NType's {@code nid} by Issuer's uuid
     * 
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
            "WHERE (v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) "
            +
            "RETURN DISTINCT n,collect(ro),collect(o),collect(rr),collect(r),collect(rv),collect(v) ORDER BY n.title")
    List<NType> findByIssuerEidAndYear(String eid, int year);

    @Query("MATCH (c:ISSUER {eid:$issuerEid})<-[:ISSUED_BY]-(n:NTYPE) WHERE n.title =~ $filter RETURN n ORDER BY n.name")
    List<NType> findByTitleFilterAndIssuerEid(String filter, String issuerEid);

    @Query("MATCH (c:ISSUER {eid:$issuerEid})<-[:ISSUED_BY]-(n:NTYPE)-[rv:VARIANTS]->(v:VARIANT), " +
            "(n)-[ro:HAS_OBVERSE]->(o:NTYPE_PART), " +
            "(n)-[rr:HAS_REVERSE]-(r:NTYPE_PART)  " +
            "WHERE n.title =~ $filter AND ((v.gregorianYear IS NOT NULL AND v.gregorianYear=$year) OR (v.gregorianYear IS NULL AND  v.minYear <= $year AND v.maxYear >= $year) "
            +
            "RETURN DISTINCT n,ro,o,rr,r ORDER BY n.name")
    List<NType> findByTitleFilterAndIssuerEidAndYear(String filter, String issuerEid, int year);

    @Query("MATCH (c:COUNTRY {numistaCode:$numistaCode})<-[*]-(n:NTYPE)-[nd:DENOMINATED_IN]->(d:DENOMINATION), " +
            "(n)-[hc:HAS_COLLECTIBLE_TYPE]->(ct:COLLECTIBLE_TYPE)<-[hctc:HAS_COLLECTIBLE_TYPE_CHILD*]-(ctc:COLLECTIBLE_TYPE), "
            + "(n)-[ho:HAS_OBVERSE]->(o:NTYPE_PART), (n)-[hr:HAS_REVERSE]->(r:NTYPE_PART) "
            +
            "MATCH (n)-[ib:ISSUED_BY]->(i:ISSUER) " +
            "WHERE (($denominationNid IS NULL OR $denominationNid = '') OR " +
            "     (EXISTS((n)-[:DENOMINATED_IN]->(:DENOMINATION {nid:$denominationNid})))) " +
            "AND (($issuerCode IS NULL OR $issuerCode = '') OR " +
            "     (EXISTS((n)-[:ISSUED_BY]->(:ISSUER {code:$issuerCode})))) " +
            "AND (($subjectNumistaCode IS NULL OR $subjectNumistaCode = '') OR " +
            "     (EXISTS((n)-[:ISSUED_BY]->(:ISSUER)-[:PARENT_SUBJECT]->(:SUBJECT {numistaCode:$subjectNumistaCode})))) "
            +
            "AND (($collectibleTypeCode IS NULL OR $collectibleTypeCode = '') OR " +
            "     (EXISTS((n)-[:HAS_COLLECTIBLE_TYPE]->(:COLLECTIBLE_TYPE)<-[:HAS_COLLECTIBLE_TYPE_CHILD*]-(:COLLECTIBLE_TYPE {code:$collectibleTypeCode})))) "
            +
            "AND (($textFilter IS NULL OR $textFilter = '') OR " +
            "(lower(n.title) CONTAINS $textFilter " +
            "OR lower(o.description) CONTAINS $textFilter OR lower(o.lettering) CONTAINS $textFilter " +
            "OR lower(o.unabridgedLegend) CONTAINS $textFilter OR lower(o.letteringTranslation) CONTAINS $textFilter " +
            "OR lower(o.letteringTranslationRu) CONTAINS $textFilter " +
            "OR lower(r.description) CONTAINS $textFilter OR lower(r.lettering) CONTAINS $textFilter " +
            "OR lower(r.unabridgedLegend) CONTAINS $textFilter OR lower(r.letteringTranslation) CONTAINS $textFilter " +
            "OR lower(r.letteringTranslationRu) CONTAINS $textFilter " +
            "OR lower(i.name) CONTAINS $textFilter)) " +

            "RETURN DISTINCT n,nd,d,hc,ct,hctc,ctc,ib,i ORDER BY d.numericValue")
    List<NType> findNTypesByCountryNumistaCodeWithFilters(String numistaCode,
            String denominationNid, String issuerCode,
            String subjectNumistaCode, String collectibleTypeCode, String textFilter);

    @Query("MATCH (c:SUBJECT {numistaCode:$numistaCode})<-[*]-(n:NTYPE)-[nd:DENOMINATED_IN]->(d:DENOMINATION), " +
            "(n)-[hc:HAS_COLLECTIBLE_TYPE]->(ct:COLLECTIBLE_TYPE)<-[hctc:HAS_COLLECTIBLE_TYPE_CHILD*]-(ctc:COLLECTIBLE_TYPE) "
            + "(n)-[ho:HAS_OBVERSE]->(o:NTYPE_PART), (n)-[hr:HAS_REVERSE]->(r:NTYPE_PART) "
            +
            "MATCH (n)-[ib:ISSUED_BY]->(i:ISSUER)" +
            "WHERE (($denominationNid IS NULL OR $denominationNid = '') OR " +
            "     (EXISTS((n)-[:DENOMINATED_IN]->(:DENOMINATION {nid:$denominationNid})))) " +
            "AND (($issuerCode IS NULL OR $issuerCode = '') OR " +
            "     (EXISTS((n)-[:ISSUED_BY]->(:ISSUER {code:$issuerCode})))) " +
            "AND (($subjectNumistaCode IS NULL OR $subjectNumistaCode = '') OR " +
            "     (EXISTS((n)-[:ISSUED_BY]->(:ISSUER)-[:PARENT_SUBJECT]->(:SUBJECT {numistaCode:$subjectNumistaCode})))) "
            + "AND (($collectibleTypeCode IS NULL OR $collectibleTypeCode = '') OR " +
            "     (EXISTS((n)-[:HAS_COLLECTIBLE_TYPE]->(:COLLECTIBLE_TYPE)<-[:HAS_COLLECTIBLE_TYPE_CHILD*]-(:COLLECTIBLE_TYPE {code:$collectibleTypeCode})))) "
            +
            "AND (($textFilter IS NULL OR $textFilter = '') OR " +
            "(lower(n.title) CONTAINS $textFilter " +
            "OR lower(o.description) CONTAINS $textFilter OR lower(o.lettering) CONTAINS $textFilter " +
            "OR lower(o.unabridgedLegend) CONTAINS $textFilter OR lower(o.letteringTranslation) CONTAINS $textFilter " +
            "OR lower(o.letteringTranslationRu) CONTAINS $textFilter " +
            "OR lower(r.description) CONTAINS $textFilter OR lower(r.lettering) CONTAINS $textFilter " +
            "OR lower(r.unabridgedLegend) CONTAINS $textFilter OR lower(r.letteringTranslation) CONTAINS $textFilter " +
            "OR lower(r.letteringTranslationRu) CONTAINS $textFilter " +
            "OR lower(i.name) CONTAINS $textFilter)) " +

            "RETURN DISTINCT n,nd,d,hc,ct,hctc,ctc,ib,i ORDER BY d.numericValue")
    List<NType> findNTypesBySubjectNumistaCodeWithFilters(String numistaCode,
            String denominationNid, String issuerCode,
            String subjectNumistaCode, String collectibleTypeCode);

    @Query("MATCH (i:ISSUER {code:$code})<-[ib:ISSUED_BY]-(n:NTYPE)-[nd:DENOMINATED_IN]->(d:DENOMINATION), " +
            "(n)-[hc:HAS_COLLECTIBLE_TYPE]->(ct:COLLECTIBLE_TYPE)<-[hctc:HAS_COLLECTIBLE_TYPE_CHILD*]-(ctc:COLLECTIBLE_TYPE) "
            + "WHERE (($collectibleTypeCode IS NULL OR $collectibleTypeCode = '') OR " +
            "     (EXISTS((n)-[:HAS_COLLECTIBLE_TYPE]->(:COLLECTIBLE_TYPE)<-[:HAS_COLLECTIBLE_TYPE_CHILD*]-(:COLLECTIBLE_TYPE {code:$collectibleTypeCode})))) "
            +
            "RETURN DISTINCT n,nd,d,hc,ct,hctc,ctc,ib,i ORDER BY d.numericValue")
    List<NType> findNTypesByIssuerCodeWithFilter(String code, String collectibleTypeCode);

}

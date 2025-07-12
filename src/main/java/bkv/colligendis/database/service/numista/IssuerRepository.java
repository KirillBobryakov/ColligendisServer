package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import bkv.colligendis.rest.catalogue.CSIItem;

import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface IssuerRepository extends AbstractNeo4jRepository<Issuer> {

    List<Issuer> findByCodeOrName(String code, String name);

    /**
     * Find issuer's {@code uuid} by {@code  name}.
     * 
     * @param name Issuer's name
     * @return If Issuer with {@code name} exists, then return issuer's uuid like
     *         String, or return NULL
     */
    @Query("MATCH (n:ISSUER) WHERE n.name = $name RETURN n.uuid")
    String findIssuerUuidByName(String name);

    /**
     * Find issuer's {@code uuid} by {@code  code}.
     * 
     * @param code Issuer's code
     * @return If Issuer with {@code code} exists, then return issuer's UUID like
     *         String
     */
    @Query("MATCH (n:ISSUER) WHERE n.code = $code RETURN n.uuid")
    String findUuidByCode(String code);

    /**
     * Find issuer's {@code code} by {@code  uuid}.
     * 
     * @param uuid Issuer's uuid
     * @return If Issuer with {@code uuid} exists, then return issuer's code or null
     */
    @Query("MATCH (n:ISSUER) WHERE n.uuid = $uuid RETURN n.code")
    String findIssuerCodeByUuid(String uuid);

    @Query("MATCH (i:ISSUER) WHERE i.name =~ $searchNameRegex OPTIONAL MATCH (i)<-[int:ISSUED_BY]-(n:NTYPE) RETURN DISTINCT i, collect(int), collect(n)")
    List<Issuer> findByNameContainingIgnoreCase(String searchNameRegex);

    @Query("MATCH (:COUNTRY {eid:$eid})-[*1..4]-(s:ISSUER) return (s)")
    List<Issuer> findByCountryEid(String eid);

    @Query("MATCH (:SUBJECT {eid:$eid})-[:CONTAINS_ISSUER]->(s:ISSUER) return (s)")
    List<Issuer> findBySubjectEid(String eid);

    /**
     * Check is Issuer's code equals to {@code code}.
     * This Query is needed to know is new issuer's code equal to old one before
     * {@code updateCode} method.
     *
     * @param uuid Unique field for searching Issuer
     * @param code value for checking
     * @return {@code true} if equal, or {@code false} if not
     */
    @Query("MATCH (n:ISSUER) WHERE n.uuid = $uuid RETURN COALESCE(n.code, 'NOT SET') = $code")
    Boolean isIssuerCodeEqualsTo(String uuid, String code);

    /**
     * Use this method for update issuer's code on new one.
     *
     * @param uuid Unique field for searching Issuer
     * @param code new issuer's code
     */
    @Query("MATCH (n:ISSUER) WHERE n.uuid = $uuid SET n.code = $code")
    void updateIssuerCode(String uuid, String code);

    /**
     * Find Issuer by {@code numistaCode}
     * 
     * @param numistaCode Issuer's numistaCode
     * @return Issuer if exists, or null
     */
    @Query("MATCH (i:ISSUER) WHERE i.code = $numistaCode RETURN i")
    Issuer findIssuerByNumistaCode(String numistaCode);

    @Query("MATCH (i:ISSUER {uuid:$issuerUuid}), (c:COUNTRY {uuid:$countryUuid}) MERGE (i)-[r:"
            + Issuer.RELATE_TO_COUNTRY + "]->(c) RETURN count(r) > 0")
    boolean connectToCountry(String issuerUuid, String countryUuid);

    @Query("MATCH (i:ISSUER {uuid:$issuerUuid}), (ps:SUBJECT {uuid:$parentSubjectUuid}) MERGE (i)-[r:"
            + Issuer.PARENT_SUBJECT + "]->(ps) RETURN count(r) > 0")
    boolean connectToParentSubject(String issuerUuid, String parentSubjectUuid);

    // Client Catalogue Service

    @Query("match (n:ISSUER) WHERE toLower(n.name) =~ $filter OR ANY (altName in n.ruAlternativeNames WHERE toLower(altName) =~ $filter)  RETURN n.code as code, n.name as name LIMIT 5")
    List<CSIItem> findCSItemsByName(String filter);

    @Query("MATCH (i:ISSUER)<-[*]-(n:NTYPE) WHERE i.code=$numistaCode return count(n)")
    Integer countChildrenNTypes(String numistaCode);

    /**
     * Find children issuers of a country
     * 
     * @param subjectNumistaCode Country's numistaCode
     * @return List of children issuers
     */
    @Query("MATCH (s:SUBJECT)<-[*]-(i:ISSUER) WHERE s.numistaCode=$subjectNumistaCode RETURN DISTINCT i ORDER BY i.name")
    List<Issuer> findChildrenIssuersBySubjectNumistaCode(String subjectNumistaCode);

    /**
     * Find children issuers of a country
     * 
     * @param countryNumistaCode Country's numistaCode
     * @return List of children issuers
     */
    @Query("MATCH (c:COUNTRY)<-[*]-(i:ISSUER) WHERE c.numistaCode=$countryNumistaCode RETURN DISTINCT i ORDER BY i.name")
    List<Issuer> findChildrenIssuersByCountryNumistaCode(String countryNumistaCode);

}

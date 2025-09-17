package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Currency;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface CurrencyRepository extends AbstractNeo4jRepository<Currency> {

    Currency findByUuid(String uuid);

    Currency findByFullName(String fullName);

    Currency findByName(String name);

    /**
     * Find Currency's nid by Currency's uuid
     * 
     * @param uuid Currency's uuid
     * @return Currency's nid
     */
    @Query("MATCH (n:CURRENCY) WHERE n.uuid = $uuid RETURN n.nid")
    String findCurrencyNidByUuid(String uuid);

    Currency findCurrencyByNid(String nid);

    List<Currency> findByIssuerCode(String issuerCode);

    /**
     * Find currencies that have a relationship to a country with the specified
     * numistaCode, including their denominations
     * 
     * @param numistaCode The numista code of the country
     * @return List of currencies related to the country with denominations
     */
    @Query("MATCH (c:COUNTRY {numistaCode: $numistaCode})<-[*]-(n:NTYPE)-[*]->(cur:CURRENCY) RETURN DISTINCT cur.uuid")
    List<String> findCurrenciesUuidsByCountryNumistaCode(String numistaCode);

    @Query("MATCH (c:COUNTRY)<-[*]-(:ISSUER)<-[:ISSUED_BY]-(n:NTYPE)-[:UNDER_CURRENCY]->(cur:CURRENCY) WHERE c.numistaCode=$countryNumistaCode RETURN DISTINCT cur")
    List<Currency> findCurrenciesByCountryNumistaCode(String countryNumistaCode);

    /**
     * Find currencies that have a relationship to a subject with the specified
     * numistaCode, including their denominations
     * 
     * @param numistaCode The numista code of the subject
     * @return List of currencies related to the subject with denominations
     */
    @Query("MATCH (s:SUBJECT {numistaCode: $numistaCode})<-[*]-(n:NTYPE)-[*]->(cur:CURRENCY) RETURN DISTINCT cur.uuid")
    List<String> findCurrenciesUuidsBySubjectNumistaCode(String numistaCode);

    @Query("MATCH (s:SUBJECT)<-[*]-(:ISSUER)<-[:ISSUED_BY]-(n:NTYPE)-[:UNDER_CURRENCY]->(cur:CURRENCY) WHERE s.numistaCode=$subjectNumistaCode RETURN DISTINCT cur")
    List<Currency> findCurrenciesBySubjectNumistaCode(String subjectNumistaCode);

    /**
     * Find currencies that have a relationship to an issuer with the specified
     * code, including their denominations
     * 
     * @param code The code of the issuer
     * @return List of currencies related to the issuer with denominations
     */
    @Query("MATCH (i:ISSUER {code: $code})<-[*]-(n:NTYPE)-[*]->(cur:CURRENCY) RETURN DISTINCT cur.uuid")
    List<String> findCurrenciesUuidsByIssuerCode(String code);

    @Query("MATCH (i:ISSUER)<-[:ISSUED_BY]-(n:NTYPE)-[:UNDER_CURRENCY]->(cur:CURRENCY) WHERE i.code=$issuerCode RETURN DISTINCT cur")
    List<Currency> findCurrenciesByIssuerCode(String issuerCode);
}

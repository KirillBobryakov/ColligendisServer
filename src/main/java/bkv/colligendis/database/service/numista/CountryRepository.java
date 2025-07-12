package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import bkv.colligendis.rest.catalogue.CSIItem;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;

public interface CountryRepository extends AbstractNeo4jRepository<Country> {

    /**
     * Find an COUNTRY's UUID by {@code name}
     * 
     * @param name COUNTRY's name
     * @return COUNTRY's UUID in String value
     */
    @Query("MATCH (n:COUNTRY) WHERE n.name=$name RETURN n.uuid")
    String findCountryUuidByName(String name);

    // Flux<Country> findByNameContainingIgnoreCase(String nameFilter);

    @Query("MATCH (n:COUNTRY) RETURN n.name ORDER BY n.name")
    List<String> findAllCountriesNames();

    @Query("MATCH (c:COUNTRY)  WHERE c.name =~ $searchNameRegex " +
            "OPTIONAL MATCH (c)-[cs:CONTAINS_CHILD_SUBJECT]->(s:SUBJECT) " +
            "OPTIONAL MATCH (c)-[ci:CONTAINS_ISSUER]->(i:ISSUER) " +
            "RETURN DISTINCT c, collect(cs), collect(s), collect(ci), collect(i)")
    List<Country> findByNameContainingIgnoreCase(String searchNameRegex);

    /**
     * Find Country by {@numistaCode numistaCode}
     * 
     * @param numistaCode Country's numistaCode
     * @return Country if exists, or null
     */
    // @Query("MATCH (c:COUNTRY) WHERE c.numistaCode = $numistaCode RETURN c")
    Country findCountryByNumistaCode(String numistaCode);

    @Query("MATCH (c:COUNTRY {uuid:$countryUuid}), (ps:SUBJECT {uuid:$parentSubjectUuid}) MERGE (c)-[r:"
            + Country.PARENT_SUBJECT + "]->(ps) RETURN count(r) > 0")
    boolean connectToParentSubject(String countryUuid, String parentSubjectUuid);

    // Client Catalogue Service

    @Query("MATCH (n:COUNTRY) WHERE toLower(n.name) =~ $filter OR ANY (altName in n.ruAlternativeNames WHERE toLower(altName) =~ $filter)  RETURN n.numistaCode as code, n.name as name LIMIT 5")
    List<CSIItem> findCSItemsByName(String filter);

    /**
     * Count children subjects of a country
     * 
     * @param numistaCode Country's numistaCode
     * @return Number of children subjects
     */
    @Query("MATCH (c:COUNTRY)<-[*]-(s:SUBJECT) WHERE c.numistaCode=$countryNumistaCode RETURN count(s)")
    Integer countChildrenSubjects(String countryNumistaCode);

    /**
     * Count children issuers of a country
     * 
     * @param numistaCode Country's numistaCode
     * @return Number of children issuers
     */
    @Query("MATCH (c:COUNTRY)<-[*]-(i:ISSUER) WHERE c.numistaCode=$countryNumistaCode RETURN count(i)")
    Integer countChildrenIssuers(String countryNumistaCode);

    /**
     * Count children nTypes of a country
     * 
     * @param numistaCode Country's numistaCode
     * @return Number of children nTypes
     */
    @Query("MATCH (c:COUNTRY)<-[*]-(n:NTYPE) WHERE c.numistaCode=$numistaCode RETURN count(n)")
    Integer countChildrenNTypes(String numistaCode);

    @Query("MATCH (s:SUBJECT)-[*]->(c:COUNTRY) WHERE s.numistaCode=$subjectNumistaCode RETURN c")
    Country getParentCountryBySubjectNumistaCode(String subjectNumistaCode);

    @Query("MATCH (i:ISSUER)-[*]->(c:COUNTRY) WHERE i.code=$issuerNumistaCode RETURN c")
    Country getParentCountryByIssuerNumistaCode(String issuerNumistaCode);

}

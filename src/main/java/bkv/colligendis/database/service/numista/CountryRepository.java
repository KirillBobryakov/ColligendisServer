package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CountryRepository extends AbstractNeo4jRepository<Country> {


    /**
     * Find an COUNTRY's UUID by {@code name}
     * @param name COUNTRY's name
     * @return COUNTRY's UUID in String value
     */
    @Query("MATCH (n:COUNTRY) WHERE n.name=$name RETURN n.uuid")
    String findCountryUuidByName(String name);


//    Flux<Country> findByNameContainingIgnoreCase(String nameFilter);

    @Query("MATCH (n:COUNTRY) RETURN n.name ORDER BY n.name")
    List<String> findAllCountriesNames();

    @Query("MATCH (c:COUNTRY)  WHERE c.name =~ $searchNameRegex " +
            "OPTIONAL MATCH (c)-[cs:CONTAINS_CHILD_SUBJECT]->(s:SUBJECT) " +
            "OPTIONAL MATCH (c)-[ci:CONTAINS_ISSUER]->(i:ISSUER) " +
            "RETURN DISTINCT c, collect(cs), collect(s), collect(ci), collect(i)")
    List<Country> findByNameContainingIgnoreCase(String searchNameRegex);




    /**
     * Find Country by {@numistaCode numistaCode}
     * @param numistaCode Country's numistaCode
     * @return Country if exists, or null
     */
    // @Query("MATCH (c:COUNTRY) WHERE c.numistaCode = $numistaCode RETURN c")
    Country findCountryByNumistaCode(String numistaCode);


    @Query("MATCH (c:COUNTRY {uuid:$countryUuid}), (ps:SUBJECT {uuid:$parentSubjectUuid}) MERGE (c)-[r:" + Country.PARENT_SUBJECT + "]->(ps) RETURN count(r) > 0")
    boolean connectToParentSubject(String countryUuid, String parentSubjectUuid);
}

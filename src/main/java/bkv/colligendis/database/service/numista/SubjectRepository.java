package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface SubjectRepository extends AbstractNeo4jRepository<Subject> {


    /**
     * Find a Subject's UUID by unique field {@code name}
     * @param name unique Subject's field {@code name}
     * @return Subject's uuid in String value if exists, or null
     */
    @Query("MATCH (n:SUBJECT) WHERE n.name = $name RETURN n.uuid")
    String findSubjectUuidByName(String name);

    @Query("MATCH (s:SUBJECT) WHERE s.name =~ $searchNameRegex OPTIONAL MATCH (s)-[scs:CONTAINS_CHILD_SUBJECT]->(cs:SUBJECT) OPTIONAL MATCH (s)-[si:CONTAINS_ISSUER]->(i:ISSUER) RETURN DISTINCT s, collect(scs), collect(cs), collect(si), collect(i)")
    List<Subject> findByNameContainingIgnoreCase(String searchNameRegex);

    @Query("MATCH (:COUNTRY {eid:$countryEid})-[*1..4]-(s:SUBJECT) return (s)")
    List<Subject> findByCountryEid(String countryEid);

    @Query("MATCH (s:SUBJECT) WHERE s.name = $name RETURN s")
    Subject findSubjectByName(String name);

    // @Query("MATCH (s:SUBJECT)-[r:RELATE_TO_COUNTRY]->(c:COUNTRY), (s)-[r2:PARENT_SUBJECT]->(ps:SUBJECT) WHERE s.numistaCode = $numistaCode RETURN s,r,c,r2,ps")
    Subject findSubjectByNumistaCode(String numistaCode);




    /**
     * Check if subject exists by numista code
     * @param numistaCode
     * @return true if subject exists, false otherwise
     */
    @Query("MATCH (s:SUBJECT) WHERE s.numistaCode = $numistaCode RETURN COUNT(s) > 0")
    boolean isSubjectExists(String numistaCode);


    @Query("MATCH (s:SUBJECT {uuid:$subjectUuid}), (ps:SUBJECT {uuid:$parentSubjectUuid}) MERGE (s)-[r:" + Subject.PARENT_SUBJECT + "]->(ps) RETURN count(r) > 0")
    boolean connectToParentSubject(String subjectUuid, String parentSubjectUuid);

    @Query("MATCH (s:SUBJECT {uuid:$subjectUuid}), (c:COUNTRY {uuid:$countryUuid}) MERGE (s)-[r:" + Subject.RELATE_TO_COUNTRY + "]->(c) RETURN count(r) > 0")
    boolean connectToCountry(String subjectUuid, String countryUuid);

}

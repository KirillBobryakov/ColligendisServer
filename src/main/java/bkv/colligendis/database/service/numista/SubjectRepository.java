package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Country;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubjectRepository extends AbstractNeo4jRepository<Subject> {

    Mono<Subject> findByName(String name);

    Flux<Subject> findByNameContainingIgnoreCase(String nameFilter);

    @Query("MATCH (:COUNTRY {eid:$countryEid})-[*1..4]-(s:SUBJECT) return (s)")
    Flux<Subject> findByCountryEid(String countryEid);

}

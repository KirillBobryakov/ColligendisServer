package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.Subject;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IssuerRepository extends AbstractNeo4jRepository<Issuer> {

    Mono<Issuer> findByName(String name);
    Mono<Issuer> findByCode(String code);

    Flux<Issuer> findByNameContainingIgnoreCase(String nameFilter);

    @Query("MATCH (:COUNTRY {eid:$eid})-[*1..4]-(s:ISSUER) return (s)")
    Flux<Issuer> findByCountryEid(String eid);

    @Query("MATCH (:SUBJECT {eid:$eid})-[:CONTAINS_ISSUER]->(s:ISSUER) return (s)")
    Flux<Issuer> findBySubjectEid(String eid);

}

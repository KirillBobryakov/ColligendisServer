package bkv.colligendis.database.service;

import bkv.colligendis.database.entity.AbstractEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

public interface AbstractNeo4jRepository<E extends AbstractEntity> extends ReactiveNeo4jRepository<E, Long> {


//    @Query("MATCH (n {eid:$eid}) RETURN n")
    Mono<E> findByEid(String eid);

    @Query("MATCH (n)-[r]-() WHERE n.eid=$eid RETURN count(r)")
    long countRelationships(String eid);

    @Query("MATCH (n) WHERE n.eid=$eid DETACH DELETE n")
    void deleteWithDetach(String eid);




}

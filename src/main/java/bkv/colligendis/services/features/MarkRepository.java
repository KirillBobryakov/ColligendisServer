//package bkv.colligendis.services.features;
//
//import bkv.colligendis.database.entity.numista.Mintmark;
//import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
//import org.springframework.data.neo4j.repository.query.Query;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//public interface MarkRepository extends ReactiveNeo4jRepository<Mintmark, Long> {
//
//    Mono<Mintmark> findByNumistaURL(String numistaURL);
//    Mono<Mintmark> findByName(String name);
//
//    Flux<Mintmark> findByNameContainingIgnoreCase(String nameFilter);
//
//    @Query("MATCH (m:MARK)-[r]-() WHERE id(m)=$markId RETURN count(r)")
//    Mono<Integer> countRelations(long markId);
//
//}

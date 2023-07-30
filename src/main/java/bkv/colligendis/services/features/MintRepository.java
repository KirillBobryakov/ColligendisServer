//package bkv.colligendis.services.features;
//
//import bkv.colligendis.database.entity.numista.Mint;
//import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
//import org.springframework.data.neo4j.repository.query.Query;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//public interface MintRepository extends ReactiveNeo4jRepository<Mint, Long> {
//
//    Mono<Mint> findByNumistaURL(String numistaURL);
//    Mono<Mint> findByName(String name);
//
//    Flux<Mint> findByNameContainingIgnoreCase(String nameFilter);
//
//    @Query("MATCH (m:MINT)-[r]-() WHERE id(m)=$mintId RETURN count(r)")
//    Mono<Integer> countRelations(long mintId);
//
//}

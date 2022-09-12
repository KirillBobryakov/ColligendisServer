package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.Mint;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface MintRepository extends ReactiveNeo4jRepository<Mint, Long> {

    Mono<Mint> findByNumistaURL(String numistaURL);

}

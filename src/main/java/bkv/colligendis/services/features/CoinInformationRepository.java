package bkv.colligendis.services.features;


import bkv.colligendis.database.entity.piece.CoinInformation;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface CoinInformationRepository extends ReactiveNeo4jRepository<CoinInformation, Long> {


    Mono<CoinInformation> findByNumistaNumber(String numistaNumber);
}

package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.PieceSide;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface CoinPartRepository extends ReactiveNeo4jRepository<PieceSide, Long> {
}

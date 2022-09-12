package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.CoinComment;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface CoinCommentRepository extends ReactiveNeo4jRepository<CoinComment, Long> {
}

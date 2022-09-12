package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.CoinVariant;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface CoinVariantRepository extends ReactiveNeo4jRepository<CoinVariant, Long> {


}

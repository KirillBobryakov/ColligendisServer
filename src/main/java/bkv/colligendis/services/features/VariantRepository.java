package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.Variant;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface VariantRepository extends ReactiveNeo4jRepository<Variant, Long> {


}

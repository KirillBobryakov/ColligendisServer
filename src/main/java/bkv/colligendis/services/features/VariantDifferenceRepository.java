package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.item.VariantDifference;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

public interface VariantDifferenceRepository extends AbstractNeo4jRepository<VariantDifference> {


}

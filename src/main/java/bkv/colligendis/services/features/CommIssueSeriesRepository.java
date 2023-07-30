package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.item.CommIssueSeries;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface CommIssueSeriesRepository extends AbstractNeo4jRepository<CommIssueSeries> {

    Mono<CommIssueSeries> findByName(String name);
}

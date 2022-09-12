package bkv.colligendis.services.features;

import bkv.colligendis.database.entity.piece.CommIssueSeries;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface CommIssueSeriesRepository extends ReactiveNeo4jRepository<CommIssueSeries, Long> {

    Mono<CommIssueSeries> findByName(String name);
}

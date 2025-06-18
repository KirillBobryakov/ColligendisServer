package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Issuer;
import bkv.colligendis.database.entity.numista.IssuingEntity;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IssuingEntityRepository extends AbstractNeo4jRepository<IssuingEntity> {

    IssuingEntity findByName(String name);
    IssuingEntity findIssuingEntityByCode(String code);

    List<IssuingEntity> findByIssuerCode(String issuerCode);

}

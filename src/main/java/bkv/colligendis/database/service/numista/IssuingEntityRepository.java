package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.IssuingEntity;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import java.util.List;

public interface IssuingEntityRepository extends AbstractNeo4jRepository<IssuingEntity> {

    IssuingEntity findByName(String name);
    IssuingEntity findIssuingEntityByCode(String code);

    List<IssuingEntity> findByIssuerCode(String issuerCode);

}

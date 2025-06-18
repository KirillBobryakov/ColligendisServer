package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface TypeRepository extends AbstractNeo4jRepository<CollectibleType> {

    CollectibleType findByCode(String code);
}

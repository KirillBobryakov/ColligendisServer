package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CompositionType;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface CompositionTypeRepository extends AbstractNeo4jRepository<CompositionType> {

    CompositionType findByCode(String code);

}

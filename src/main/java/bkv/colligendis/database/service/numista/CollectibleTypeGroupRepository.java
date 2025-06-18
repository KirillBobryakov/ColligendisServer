package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CollectibleTypeGroup;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface CollectibleTypeGroupRepository extends AbstractNeo4jRepository<CollectibleTypeGroup> {

    CollectibleTypeGroup findByName(String name);
}

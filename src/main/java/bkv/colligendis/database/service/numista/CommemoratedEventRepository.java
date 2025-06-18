package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.CommemoratedEvent;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface CommemoratedEventRepository extends AbstractNeo4jRepository<CommemoratedEvent> {

    CommemoratedEvent findByName(String name);

}

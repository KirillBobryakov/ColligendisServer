package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mark;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface MarkRepository extends AbstractNeo4jRepository<Mark> {

    Mark findByNid(String nid);
}

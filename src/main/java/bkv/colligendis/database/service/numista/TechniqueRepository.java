package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Technique;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface TechniqueRepository extends AbstractNeo4jRepository<Technique> {

    Technique findByNid(String nid);
}

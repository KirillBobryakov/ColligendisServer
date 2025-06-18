package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Metal;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface MetalRepository extends AbstractNeo4jRepository<Metal> {

    Metal findByNid(String nid);
}

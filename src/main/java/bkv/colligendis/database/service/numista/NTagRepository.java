package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.NTag;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface NTagRepository extends AbstractNeo4jRepository<NTag> {

    NTag findByNid(String nid);
}

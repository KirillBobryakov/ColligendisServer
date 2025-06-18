package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.LetteringScript;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface LetteringScriptRepository extends AbstractNeo4jRepository<LetteringScript> {

    LetteringScript findByNid(String nid);
}

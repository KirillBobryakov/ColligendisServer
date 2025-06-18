package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mintmark;
import bkv.colligendis.database.entity.numista.SpecifiedMint;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import reactor.core.publisher.Mono;

public interface MintmarkRepository extends AbstractNeo4jRepository<Mintmark> {

    Mintmark findByNid(String nid);
}

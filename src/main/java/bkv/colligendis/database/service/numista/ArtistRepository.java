package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Artist;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface ArtistRepository extends AbstractNeo4jRepository<Artist> {

    Artist findByNid(String nid);

}

package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mint;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;

public interface MintRepository extends AbstractNeo4jRepository<Mint> {

    Mint findByNid(String nid);
    Mint findByNumistaURL(String numistaURL);
    Mint findByName(String name);

    List<Mint> findByNameContainingIgnoreCase(String nameFilter);

    @Query("MATCH (m:MINT)-[r]-() WHERE id(m)=$mintId RETURN count(r)")
    Integer countRelations(String mintId);

}

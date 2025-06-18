package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

public interface RulerRepository extends AbstractNeo4jRepository<Ruler> {

    /**
     *  Find ruler's {@code eid} by {@code  name}.
     * @param name Ruler's name
     * @return If Ruler with {@code name} exists, then return ruler's eid like String, or return NULL
     */
    @Query("MATCH (n:RULER) WHERE n.name = $name RETURN n.eid")
    String findByName(String name);

    /**
     *  Find Ruler's {@code nid} by {@code  nid}.
     * @param nid Ruler's nid
     * @return If Ruler with {@code nid} exists, then return Ruler's eid like String
     */
    @Query("MATCH (n:RULER) WHERE n.nid = $nid RETURN n.eid")
    String findByNid(String nid);
}

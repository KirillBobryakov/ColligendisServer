package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.RulerGroup;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface RulerGroupRepository extends AbstractNeo4jRepository<RulerGroup> {

    /**
     *  Find RulerGroup's {@code UUID} by {@code  name}.
     * @param name RulerGroup's name
     * @return If RulerGroup with {@code name} exists, then return RulerGroup's UUID like String, or return NULL
     */
    @Query("MATCH (n:RULER_GROUP) WHERE n.name = $name RETURN n.uuid")
    String findUuidByName(String name);

    /**
     *  Find RulerGroup by {@code  nid}.
     * @param nid RulerGroup's nid
     * @return If RulerGroup with {@code nid} exists, then return RulerGroup
     */
    RulerGroup findRulerGroupByNid(String nid);
}

package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Ruler;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface RulerRepository extends AbstractNeo4jRepository<Ruler> {

    /**
     * Find ruler's {@code eid} by {@code  name}.
     *
     * @param name Ruler's name
     * @return If Ruler with {@code name} exists, then return ruler's eid like String, or return NULL
     */
    @Query("MATCH (n:RULER) WHERE n.name = $name RETURN n.eid")
    String findByName(String name);

    /**
     * Find Ruler by {@code  nid}.
     *
     * @param nid Ruler's nid
     * @return If Ruler with {@code nid} exists, then return Ruler
     */
    Ruler findByNid(String nid);


    /**
     * Check is a Ruler is actual. Status Actual can be used for update Rulers from Numista and keep them up to date.
     *
     * @param uuid Ruler's UUID
     * @return actual status
     */
    @Query("MATCH (n:RULER) WHERE n.uuid = $uuid RETURN n.isActual")
    Boolean isActual(String uuid);

    /**
     * Check has a Ruler any Period which starts from Year
     *
     * @param rulerUuid Ruler's UUID in String value
     * @param yearUuid  Year's UUID in String value
     * @return if period was found then return true
     */
    @Query("MATCH (r:RULER)-[rel:DURING_PERIOD]->(:PERIOD)-[:FROM]->(y:YEAR) WHERE r.uuid = $rulerUuid AND y.uuid = $yearUuid RETURN count(rel) > 0")
    Boolean hasRulerPeriodStartsFromYear(String rulerUuid, String yearUuid);

    /**
     * Check has a Ruler any Period which ends till Year
     *
     * @param rulerUuid Ruler's UUID in String value
     * @param yearUuid  Year's UUID in String value
     * @return if period was found then return true
     */
    @Query("MATCH (r:RULER)-[rel:DURING_PERIOD]->(:PERIOD)-[:TILL]->(y:YEAR) WHERE r.uuid = $rulerUuid AND y.uuid = $yearUuid RETURN count(rel) > 0")
    Boolean hasRulerPeriodEndsTillYear(String rulerUuid, String yearUuid);


    /**
     * Get Ruler's Name by UUID
     *
     * @param rulerUuid Ruler' UUID in String value
     * @return Ruler's Name
     */
    @Query("MATCH (r:RULER) WHERE r.uuid = $rulerUuid RETURN r.name")
    String getRulerNameByUuid(String rulerUuid);


    /**
     * Create relationship [:DURING_PERIOD] from Ruler to the Period
     *
     * @param rulerUuid  Ruler's UUID in String value
     * @param periodUuid Period's UUID in String value
     * @return if relationship was created then return true
     */
    @Query("MATCH (r:RULER), (p:PERIOD) WHERE r.uuid = $rulerUuid AND p.uuid = $periodUuid CREATE (r)-[rel:DURING_PERIOD]->(p) RETURN count(rel) > 0")
    public Boolean setRulerDuringPeriod(String rulerUuid, String periodUuid);


    public List<Ruler> findByIssuerCode(String issuerCode);

}

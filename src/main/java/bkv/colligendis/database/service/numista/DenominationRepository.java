package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Denomination;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface DenominationRepository extends AbstractNeo4jRepository<Denomination> {

    /**
     * Find a Denomination by nid
     * @param nid Denomination's nid
     * @return Denomination's eid (UUID) in String value, or null
     */
    @Query("MATCH (n:DENOMINATION) WHERE n.nid = $nid RETURN n.eid")
    String findEidByCode(String nid);


    /**
     * Find Denomination's nid by Denomination's uuid
     * @param uuid Denomination's uuid
     * @return Denomination's nid
     */
    @Query("MATCH (n:DENOMINATION) WHERE n.uuid = $uuid RETURN n.nid")
    String findDenominationNidByUuid(String uuid);

    /**
     * Find Denomination's uuid by unique field Denomination's nid
     * @param nid Denomination's nid
     * @return Denomination's uuid
     */
    @Query("MATCH (n:DENOMINATION) WHERE n.nid = $nid RETURN n.uuid")
    String findDenominationUuidByNid(String nid);


    Denomination findByNid(String nid);


    List<Denomination> findByCurrency_Nid(String currencyNid);
}

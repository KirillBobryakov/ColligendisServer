package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.SpecifiedMint;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface SpecifiedMintRepository extends AbstractNeo4jRepository<SpecifiedMint> {



    /**
     * Find specified mint by identifier and mint nid without mintmark
     * @param identifier
     * @param mintNid
     * @return SpecifiedMint
     */
    @Query("MATCH (n:SPECIFIED_MINT {identifier:$identifier})-[r:WITH_MINT]->(m:MINT {nid:$mintNid}) WHERE NOT (n)-[:WITH_MINTMARK]->(:MINTMARK) RETURN n,r,m")
    SpecifiedMint findByIdentifierMintWithoutMintmark(String identifier, String mintNid);


    /**
     * Find specified mint by identifier and mint nid with mintmark
     * @param identifier
     * @param mintNid
     * @param mintmarkNid
     * @return SpecifiedMint
     */
    @Query("MATCH (n:SPECIFIED_MINT {identifier:$identifier})-[rm:WITH_MINT]->(m:MINT {nid:$mintNid}), (n)-[rmm:WITH_MINTMARK]->(mm:MINTMARK {nid:$mintmarkNid}) RETURN n,rm,rmm,m,mm")
    SpecifiedMint findByIdentifierMintMintmark(String identifier, String mintNid, String mintmarkNid);



    
}

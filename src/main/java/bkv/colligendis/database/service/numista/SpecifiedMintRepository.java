package bkv.colligendis.database.service.numista;

import bkv.colligendis.database.entity.numista.Mintmark;
import bkv.colligendis.database.entity.numista.SpecifiedMint;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Mono;

public interface SpecifiedMintRepository extends AbstractNeo4jRepository<SpecifiedMint> {


    Mono<SpecifiedMint> findByEid(String eid);

    @Query("MATCH (n:SPECIFIED_MINT {identifier:$identifier})-[:WITH_MINT]->(m:MINT {nid:$mintNid}) WHERE NOT (n)-[:WITH_MINTMARK]->(:MINTMARK) RETURN n.eid")
    Mono<String> findByIdentifierMint(String identifier, String mintNid);

    @Query("MATCH (n:SPECIFIED_MINT {identifier:$identifier})-[:WITH_MINT]->(m:MINT {nid:$mintNid}), (n)-[:WITH_MINTMARK]->(mm:MINTMARK {nid:$mintmarkNid}) RETURN n.eid")
    Mono<String> findByIdentifierMintMintmark(String identifier, String mintNid, String mintmarkNid);

}

package bkv.colligendis.database.service.numista;

import org.springframework.data.neo4j.repository.query.Query;

import bkv.colligendis.database.entity.numista.CollectibleType;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface TypeRepository extends AbstractNeo4jRepository<CollectibleType> {

    CollectibleType findByCode(String code);

    @Query("MATCH (n:NTYPE {nid:$NTypeNid})-[:HAS_COLLECTIBLE_TYPE]->(c:COLLECTIBLE_TYPE) OPTIONAL MATCH (c)<-[ccc:HAS_COLLECTIBLE_TYPE_CHILD*]-(cc:COLLECTIBLE_TYPE) RETURN c, collect(cc), collect(ccc)")
    CollectibleType findByNTypeNid(String NTypeNid);
}

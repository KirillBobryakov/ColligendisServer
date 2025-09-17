package bkv.colligendis.database.service.numista;

import java.util.List;

import org.springframework.data.neo4j.repository.query.Query;

import bkv.colligendis.database.entity.numista.Item;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface ItemRepository extends AbstractNeo4jRepository<Item> {

    @Query("MATCH (v:VARIANT {nid:$variantNid})<-[:RELATED_TO_VARIANT]-(i:ITEM) RETURN i")
    List<Item> findItemsByVariantNid(String variantNid);

    @Query("MATCH (i:ITEM {uuid:$uuid}) DETACH DELETE i RETURN count(i) > 0")
    boolean deleteByUuid(String uuid);

    Integer countItemsByVariantNid(String variantNid);

    @Query("MATCH (i:ITEM {uuid:$itemUuid}), (v:VARIANT {nid:$variantNid}) MERGE (i)-[r:" + Item.RELATED_TO_VARIANT
            + "]->(v) RETURN count(r) > 0")
    boolean connectItemToVariant(String itemUuid, String variantNid);

    @Query("MATCH (i:ITEM {uuid:$itemUuid}), (u:USER {uuid:$userUuid}) MERGE (i)-[r:" + Item.BELONGS_TO_USER
            + "]->(u) RETURN count(r) > 0")
    boolean connectItemToUser(String itemUuid, String userUuid);

}

package bkv.colligendis.database.service.numista;

import java.util.List;

import org.springframework.data.neo4j.repository.query.Query;

import bkv.colligendis.database.entity.numista.Item;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface ItemRepository extends AbstractNeo4jRepository<Item> {

    List<Item> findItemsByVariantNid(String variantNid);

    @Query("MATCH (i:ITEM) WHERE i.uuid = $uuid DETACH DELETE i")
    void deleteByUuid(String uuid);

    Integer countItemsByVariantNid(String variantNid);

}

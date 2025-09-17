package bkv.colligendis.database.service.meshok;

import java.util.List;

import org.springframework.data.neo4j.repository.query.Query;

import bkv.colligendis.database.entity.meshok.MeshokLot;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface MeshokLotRepository extends AbstractNeo4jRepository<MeshokLot> {

    // @Query("MATCH (n:MESHOK_LOT) WHERE n.mid = $mid RETURN n")
    MeshokLot findByMid(int mid);

    // @Query("MATCH (n:MESHOK_LOT)-[:RELATED_TO_CATEGORY]->(c:MESHOK_CATEGORY)
    // WHERE c.mid = $mid RETURN DISTINCT n")
    List<MeshokLot> findByCategoryMid(int mid);

    @Query("MATCH (n:MESHOK_LOT)-[rtc:RELATED_TO_CATEGORY]->(c:MESHOK_CATEGORY), (n)-[rtv:RELATED_TO_VARIANT]->(v:VARIANT) RETURN n, rtc, c, rtv, v LIMIT $limit")
    List<MeshokLot> findAllLimitedWithCategory(int limit);

}

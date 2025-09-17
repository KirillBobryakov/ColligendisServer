package bkv.colligendis.database.service.meshok;

import java.util.List;

import org.springframework.data.neo4j.repository.query.Query;

import bkv.colligendis.database.entity.meshok.MeshokCategory;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface MeshokCategoryRepository extends AbstractNeo4jRepository<MeshokCategory> {

    MeshokCategory findByName(String name);

    MeshokCategory findByMid(int mid);

    @Query("match (n:MESHOK_CATEGORY {mid: $parentMid})-[:HAS_CHILD*]->(c:MESHOK_CATEGORY) return c")
    List<MeshokCategory> findByParentMid(int parentMid);

    @Query("MATCH (parent:MESHOK_CATEGORY)-[:HAS_CHILD]->(c:MESHOK_CATEGORY {mid: $childMid}) RETURN parent LIMIT 1")
    MeshokCategory findParentByChildMid(int childMid);

}

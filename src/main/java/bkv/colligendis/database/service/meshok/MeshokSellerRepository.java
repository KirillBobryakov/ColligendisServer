package bkv.colligendis.database.service.meshok;

import org.springframework.data.neo4j.repository.query.Query;

import bkv.colligendis.database.entity.meshok.MeshokSeller;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface MeshokSellerRepository extends AbstractNeo4jRepository<MeshokSeller> {

    @Query("MATCH (n:MESHOK_SELLER) WHERE n.mid = $mid RETURN n")
    MeshokSeller findByMid(int mid);
}

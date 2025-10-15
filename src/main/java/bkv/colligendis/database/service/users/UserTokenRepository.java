package bkv.colligendis.database.service.users;

import org.springframework.data.neo4j.repository.query.Query;

import bkv.colligendis.database.entity.UserToken;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface UserTokenRepository extends AbstractNeo4jRepository<UserToken> {

    UserToken findByJti(String jti);

    @Query("MATCH (u:USER)-[:HAS_TOKEN]->(t:USER_TOKEN) WHERE u.email = $email RETURN t")
    UserToken findByUserEmail(String email);
}

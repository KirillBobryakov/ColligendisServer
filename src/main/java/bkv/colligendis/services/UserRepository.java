package bkv.colligendis.services;


import bkv.colligendis.database.entity.User;
import bkv.colligendis.database.service.AbstractNeo4jRepository;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends AbstractNeo4jRepository<User> {

    Mono<User> findByEmail(String email);
    Mono<User> findByUsername(String username);
}
package bkv.colligendis.services;


import bkv.colligendis.database.entity.User;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveNeo4jRepository<User, Long> {

    Mono<User> findByEmail(String email);
    Mono<User> findByUsername(String username);
}
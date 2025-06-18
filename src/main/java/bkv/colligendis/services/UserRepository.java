package bkv.colligendis.services;


import bkv.colligendis.database.entity.User;
import bkv.colligendis.database.service.AbstractNeo4jRepository;

public interface UserRepository extends AbstractNeo4jRepository<User> {

    User findByEmail(String email);
    User findByUsername(String username);
}
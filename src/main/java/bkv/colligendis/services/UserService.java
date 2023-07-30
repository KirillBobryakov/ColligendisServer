package bkv.colligendis.services;

import bkv.colligendis.database.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractService<User, UserRepository> {


    public UserService(UserRepository repository) {
        super(repository);
    }

    public User findByEmail(String email){
        return repository.findByEmail(email).block();
    }
    public User findByUsername(String username){
        return repository.findByUsername(username).block();
    }

    @Override
    public User setPropertyValue(Long id, String name, String value) {
        return null;
    }
}

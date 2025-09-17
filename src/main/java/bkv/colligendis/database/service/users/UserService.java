package bkv.colligendis.database.service.users;

import bkv.colligendis.database.entity.User;
import bkv.colligendis.services.AbstractService;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractService<User, UserRepository> {

    public UserService(UserRepository repository) {
        super(repository);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public User findByUuid(UUID uuid) {
        return repository.findByUuid(uuid.toString());
    }

}

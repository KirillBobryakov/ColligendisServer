package bkv.colligendis.services;

import bkv.colligendis.database.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractService<User, UserRepository> {


    public UserService(UserRepository repository) {
        super(repository);
    }


}

package bkv.colligendis.database.service.users;

import org.springframework.stereotype.Service;

import bkv.colligendis.database.entity.UserToken;
import bkv.colligendis.services.AbstractService;

@Service
public class UserTokenService extends AbstractService<UserToken, UserTokenRepository> {

    public UserTokenService(UserTokenRepository repository) {
        super(repository);
    }

    public UserToken findByUserEmail(String email) {
        return repository.findByUserEmail(email);
    }

    public boolean deleteByUserEmail(String email) {
        UserToken userToken = repository.findByUserEmail(email);
        if (userToken != null) {
            repository.delete(userToken);
            return true;
        }
        return false;
    }

}

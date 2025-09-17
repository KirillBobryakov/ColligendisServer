package bkv.colligendis.services;

import org.springframework.stereotype.Service;

import bkv.colligendis.database.entity.User;
import bkv.colligendis.database.service.users.UserService;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final UserService userService;

    public RefreshTokenService(UserService userService) {
        this.userService = userService;
    }

    public void storeInDB(String email, String jti, long expiresAtMillis) {
        User user = userService.findByEmail(email);
        user.setRefreshTokenJti(jti);
        user.setRefreshTokenExpiredAt(expiresAtMillis);
        userService.save(user);
    }

    public boolean isValid(String email) {
        User user = userService.findByEmail(email);
        if (user.getRefreshTokenJti() == null || user.getRefreshTokenExpiredAt() == null)
            return false;

        Long exp = user.getRefreshTokenExpiredAt();
        return exp != null && exp > Instant.now().toEpochMilli();
    }

    public void revoke(String email) {
        User user = userService.findByEmail(email);
        user.setRefreshTokenJti(null);
        user.setRefreshTokenExpiredAt(null);
        userService.save(user);
    }

    public void rotate(String email, String oldJti, String newJti, long newExpiresAtMillis) {
        revoke(email);
        storeInDB(email, newJti, newExpiresAtMillis);
    }

    public Optional<Long> getExpiry(String email) {
        User user = userService.findByEmail(email);
        return Optional.ofNullable(user.getRefreshTokenExpiredAt());
    }
}

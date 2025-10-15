package bkv.colligendis.security;

import org.springframework.stereotype.Service;

import bkv.colligendis.database.entity.User;
import bkv.colligendis.database.entity.UserToken;
import bkv.colligendis.database.service.users.UserService;
import bkv.colligendis.database.service.users.UserTokenService;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final UserService userService;
    private final UserTokenService userTokenService;

    public RefreshTokenService(UserService userService, UserTokenService userTokenService) {
        this.userService = userService;
        this.userTokenService = userTokenService;
    }

    public void storeInDB(String email, String accessToken, String refreshToken, String jti, long accessTokenExpiresAt,
            long refreshTokenExpiresAt) {
        User user = userService.findByEmail(email);
        UserToken userToken = user.getUserToken();
        if (userToken == null) {
            userToken = UserToken.builder().accessToken(accessToken).refreshToken(refreshToken).jti(jti)
                    .accessTokenExpiresAt(accessTokenExpiresAt).refreshTokenExpiresAt(refreshTokenExpiresAt)
                    .isRevoked(false).build();
            user.setUserToken(userToken);
        } else {
            userToken.setAccessToken(accessToken);
            userToken.setRefreshToken(refreshToken);
            userToken.setJti(jti);
            userToken.setAccessTokenExpiresAt(accessTokenExpiresAt);
            userToken.setRefreshTokenExpiresAt(refreshTokenExpiresAt);
            userToken.setRevoked(false);
        }

        userService.save(user);
    }

    public boolean isValidRefreshToken(User user) {
        if (user.getUserToken() == null || user.getUserToken().getRefreshTokenExpiresAt() == null)
            return false;

        Long exp = user.getUserToken().getRefreshTokenExpiresAt();
        return exp != null && exp > Instant.now().toEpochMilli();
    }

    public void revoke(String email) {
        userTokenService.deleteByUserEmail(email);
    }

    public void rotate(String email, String accessToken, String refreshToken, String newJti,
            long newAccessTokenExpiresAt, long newRefreshTokenExpiresAt) {
        revoke(email);
        storeInDB(email, accessToken, refreshToken, newJti, newAccessTokenExpiresAt, newRefreshTokenExpiresAt);
    }

    public Optional<Long> getExpiryRefreshToken(String email) {
        User user = userService.findByEmail(email);
        return Optional.ofNullable(user.getUserToken().getRefreshTokenExpiresAt());
    }
}

package bkv.colligendis.database.entity;

import org.springframework.data.neo4j.core.schema.Node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Node("USER_TOKEN")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
public class UserToken extends AbstractEntity {

    private String accessToken;
    private String refreshToken;
    private String jti;
    private Long accessTokenExpiresAt;
    private Long refreshTokenExpiresAt;
    private boolean isRevoked;

}

package bkv.colligendis.security;

import bkv.colligendis.rest.exceptions.InvalidTokenException;
import bkv.colligendis.rest.exceptions.UserNotFoundException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import bkv.colligendis.database.entity.User;
import bkv.colligendis.database.service.users.UserService;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    @Value("${security.jwt.refresh-token.expire-length}")
    public long refreshValidityInMilliseconds;

    private SecretKey key;

    @Autowired
    private UserService userDetailsService;

    @PostConstruct
    protected void init() {
        // Encode the secret key to base64 and create a SecretKey
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String createAccessToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return Jwts.builder()
                .claims()
                .add("role", user.getRoles())
                .add("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                .and()
                .signWith(key)//
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        return Jwts.builder()
                .claims()
                .add("type", "refresh")
                .add("jti", java.util.UUID.randomUUID().toString())
                .add("role", user.getRoles())
                .add("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshValidityInMilliseconds))
                .and()
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String getJti(String token) {
        Claims claims = parseClaims(token);
        Object jti = claims.get("jti");
        return jti != null ? jti.toString() : null;
    }

    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        Object email = claims.get("email");
        return email != null ? email.toString() : null;
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = parseClaims(token);
        @SuppressWarnings("unchecked")
        List<String> role = (List<String>) claims.get("role");
        return role != null ? role : null;
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);

        // Get user from database
        User user = userDetailsService.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found for email: " + email);
        }

        if (user.getUserToken() == null) {
            throw new InvalidTokenException("No token found for user");
        }

        if (!user.getUserToken().getAccessToken().equals(token)) {
            throw new InvalidTokenException("Token mismatch - token does not belong to user");
        }

        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public Authentication createAuthenticationFromUser(User user) {
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        return !claims.getPayload().getExpiration().before(new Date());
    }
}

package bkv.colligendis.security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    // @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "7ZHg-nUf-fIJBr1viBd-ojwuL0BIcXZC_rUmPwLOn_Y=";

    // @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 1000L * 60; // 1m

    // @Value("${security.jwt.refresh-token.expire-length:2592000000}")
    public long refreshValidityInMilliseconds = 1000L * 60 * 5; // 5m

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

    public String createToken(String email, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .claims()
                .add("role", roles)
                .add("email", email)
                .issuedAt(now)
                .expiration(validity)
                .and()
                .signWith(key)//
                .compact();
    }

    public String createRefreshToken(String email, List<String> roles) {
        System.out.println("Create refresh token...");
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshValidityInMilliseconds);
        System.out.println("Current time: " + now.getTime());
        System.out.println("refreshValidityInMilliseconds: " + validity.getTime());
        return Jwts.builder()
                .claims()
                .add("type", "refresh")
                .add("jti", java.util.UUID.randomUUID().toString())
                .add("role", roles)
                .add("email", email)
                .issuedAt(now)
                .expiration(validity)
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

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String email = claims.get("email", String.class);

        // Get user from database
        User user = userDetailsService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (SignatureException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}

package bkv.colligendis.rest.exceptions;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends AuthException {
    public TokenExpiredException(String message) {
        super(message, "TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED.value());
    }
}

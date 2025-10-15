package bkv.colligendis.rest.exceptions;

import org.springframework.http.HttpStatus;

public class TokenMissingException extends AuthException {
    public TokenMissingException(String message) {
        super(message, "TOKEN_MISSING", HttpStatus.UNAUTHORIZED.value());
    }
}

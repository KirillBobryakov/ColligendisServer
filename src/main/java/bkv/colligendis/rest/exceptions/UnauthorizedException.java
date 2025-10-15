package bkv.colligendis.rest.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AuthException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value());
    }
}

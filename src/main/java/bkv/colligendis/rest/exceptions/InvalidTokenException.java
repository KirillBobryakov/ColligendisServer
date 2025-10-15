package bkv.colligendis.rest.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AuthException {
    public InvalidTokenException(String message) {
        super(message, "INVALID_TOKEN", HttpStatus.UNAUTHORIZED.value());
    }

    public InvalidTokenException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.UNAUTHORIZED.value());
    }
}

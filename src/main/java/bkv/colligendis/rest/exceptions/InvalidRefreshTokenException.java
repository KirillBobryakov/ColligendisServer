package bkv.colligendis.rest.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends AuthException {
    public InvalidRefreshTokenException(String message) {
        super(message, "INVALID_REFRESH_TOKEN", HttpStatus.UNAUTHORIZED.value());
    }

    public InvalidRefreshTokenException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.UNAUTHORIZED.value());
    }

}

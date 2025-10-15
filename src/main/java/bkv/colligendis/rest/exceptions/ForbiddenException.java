package bkv.colligendis.rest.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AuthException {
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN", HttpStatus.FORBIDDEN.value());
    }
}

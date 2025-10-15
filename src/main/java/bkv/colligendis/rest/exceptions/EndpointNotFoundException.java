package bkv.colligendis.rest.exceptions;

import org.springframework.http.HttpStatus;

public class EndpointNotFoundException extends AuthException {
    public EndpointNotFoundException(String message) {
        super(message, "ENDPOINT_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    public EndpointNotFoundException(String message, String path) {
        super(message + " - Path: " + path, "ENDPOINT_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }
}

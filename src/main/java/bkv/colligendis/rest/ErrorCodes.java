package bkv.colligendis.rest;

public final class ErrorCodes {

    // Authentication errors
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String TOKEN_MALFORMED = "TOKEN_MALFORMED";
    public static final String TOKEN_MISSING = "TOKEN_MISSING";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String ENDPOINT_NOT_FOUND = "ENDPOINT_NOT_FOUND";

    // Validation errors
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String MISSING_REQUIRED_FIELD = "MISSING_REQUIRED_FIELD";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";

    // Server errors
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String DATABASE_ERROR = "DATABASE_ERROR";

    private ErrorCodes() {
        // Utility class
    }
}

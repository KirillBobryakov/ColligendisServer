package bkv.colligendis.rest.auth;

/**
 * This class contains examples of different authentication error responses
 * that will be returned by the authentication system.
 * 
 * These examples show how different authentication scenarios will be handled
 * with specific error codes and messages.
 */
public class AuthErrorExamples {

    /**
     * Example error responses for different authentication scenarios:
     * 
     * 1. INVALID_CREDENTIALS - Wrong email/password
     * Response:
     * {
     * "errorCode": "INVALID_CREDENTIALS",
     * "message": "Invalid email or password",
     * "httpStatus": 401,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/auth/signin"
     * }
     * 
     * 2. TOKEN_EXPIRED - JWT token has expired
     * Response:
     * {
     * "errorCode": "TOKEN_EXPIRED",
     * "message": "Token has expired",
     * "httpStatus": 401,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/api/protected-endpoint"
     * }
     * 
     * 3. INVALID_TOKEN - Malformed or invalid token
     * Response:
     * {
     * "errorCode": "INVALID_TOKEN",
     * "message": "Invalid token format: JWT signature does not match",
     * "httpStatus": 401,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/api/protected-endpoint"
     * }
     * 
     * 4. TOKEN_MISSING - No token provided
     * Response:
     * {
     * "errorCode": "TOKEN_MISSING",
     * "message": "Authorization token is required",
     * "httpStatus": 401,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/api/protected-endpoint"
     * }
     * 
     * 5. USER_NOT_FOUND - User doesn't exist
     * Response:
     * {
     * "errorCode": "USER_NOT_FOUND",
     * "message": "User not found for email: user@example.com",
     * "httpStatus": 404,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/auth/signin"
     * }
     * 
     * 6. FORBIDDEN - Valid token but insufficient permissions
     * Response:
     * {
     * "errorCode": "FORBIDDEN",
     * "message": "Access denied - can only access own user information",
     * "httpStatus": 403,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/auth/users/other@example.com"
     * }
     * 
     * 7. UNAUTHORIZED - General authentication failure
     * Response:
     * {
     * "errorCode": "UNAUTHORIZED",
     * "message": "User is not authenticated",
     * "httpStatus": 401,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/api/protected-endpoint"
     * }
     * 
     * 8. USER_ALREADY_EXISTS - Registration with existing email/username
     * Response:
     * {
     * "errorCode": "USER_ALREADY_EXISTS",
     * "message": "User with this email already exists",
     * "httpStatus": 409,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/auth/register"
     * }
     */

    // This class serves as documentation only
}

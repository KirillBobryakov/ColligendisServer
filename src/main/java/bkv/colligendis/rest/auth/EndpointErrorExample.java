package bkv.colligendis.rest.auth;

/**
 * Example demonstrating the new endpoint error handling.
 * 
 * This shows how the application now handles requests to non-existent endpoints
 * with proper error responses instead of default Spring error pages.
 */
public class EndpointErrorExample {

    /**
     * Example scenarios that will now return proper JSON error responses:
     * 
     * 1. Wrong endpoint URL:
     * Request: POST /auth/signins (typo - should be /auth/signin)
     * Response:
     * {
     * "errorCode": "ENDPOINT_NOT_FOUND",
     * "message": "The requested endpoint was not found",
     * "httpStatus": 404,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/auth/signins"
     * }
     * 
     * 2. Non-existent API endpoint:
     * Request: GET /api/nonexistent
     * Response:
     * {
     * "errorCode": "ENDPOINT_NOT_FOUND",
     * "message": "The requested endpoint was not found",
     * "httpStatus": 404,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/api/nonexistent"
     * }
     * 
     * 3. Wrong HTTP method:
     * Request: GET /auth/signin (should be POST)
     * Response:
     * {
     * "errorCode": "ENDPOINT_NOT_FOUND",
     * "message": "The requested endpoint was not found",
     * "httpStatus": 404,
     * "timestamp": "2024-01-15T10:30:00",
     * "path": "/auth/signin"
     * }
     * 
     * Benefits:
     * - Consistent JSON error responses instead of HTML error pages
     * - Proper HTTP status codes (404 for not found)
     * - Clear error messages for debugging
     * - Easy client-side error handling
     */

    // This class serves as documentation only
}

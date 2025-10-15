# Authentication Error Handling

This document explains how authentication errors are handled in the Colligendis Server application and how different types of authentication failures are separated and responded to.

## Overview

The application now provides specific error responses for different authentication scenarios, making it easier for clients to handle authentication failures appropriately.

## Error Response Structure

All authentication errors follow this consistent structure:

```json
{
  "errorCode": "ERROR_CODE",
  "message": "Human readable error message",
  "httpStatus": 401,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/endpoint"
}
```

## Authentication Error Types

### 1. Invalid Credentials
- **Error Code**: `INVALID_CREDENTIALS`
- **HTTP Status**: 401
- **When**: Wrong email/password during signin
- **Example Response**:
```json
{
  "errorCode": "INVALID_CREDENTIALS",
  "message": "Invalid email or password",
  "httpStatus": 401,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/auth/signin"
}
```

### 2. Token Expired
- **Error Code**: `TOKEN_EXPIRED`
- **HTTP Status**: 401
- **When**: JWT token has expired
- **Example Response**:
```json
{
  "errorCode": "TOKEN_EXPIRED",
  "message": "Token has expired",
  "httpStatus": 401,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/protected-endpoint"
}
```

### 3. Invalid Token
- **Error Code**: `INVALID_TOKEN`
- **HTTP Status**: 401
- **When**: Malformed, tampered, or invalid token format
- **Example Response**:
```json
{
  "errorCode": "INVALID_TOKEN",
  "message": "Invalid token format: JWT signature does not match",
  "httpStatus": 401,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/protected-endpoint"
}
```

### 4. Token Mismatch
- **Error Code**: `INVALID_TOKEN`
- **HTTP Status**: 401
- **When**: Token doesn't belong to the user
- **Example Response**:
```json
{
  "errorCode": "INVALID_TOKEN",
  "message": "Token mismatch - token does not belong to user",
  "httpStatus": 401,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/protected-endpoint"
}
```

### 5. User Not Found
- **Error Code**: `USER_NOT_FOUND`
- **HTTP Status**: 404
- **When**: User doesn't exist in database
- **Example Response**:
```json
{
  "errorCode": "USER_NOT_FOUND",
  "message": "User not found for email: user@example.com",
  "httpStatus": 404,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/auth/signin"
}
```

### 6. Forbidden Access
- **Error Code**: `FORBIDDEN`
- **HTTP Status**: 403
- **When**: Valid token but insufficient permissions
- **Example Response**:
```json
{
  "errorCode": "FORBIDDEN",
  "message": "Access denied - can only access own user information",
  "httpStatus": 403,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/auth/users/other@example.com"
}
```

### 7. Unauthorized
- **Error Code**: `UNAUTHORIZED`
- **HTTP Status**: 401
- **When**: General authentication failure
- **Example Response**:
```json
{
  "errorCode": "UNAUTHORIZED",
  "message": "User is not authenticated",
  "httpStatus": 401,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/protected-endpoint"
}
```

### 8. User Already Exists
- **Error Code**: `USER_ALREADY_EXISTS`
- **HTTP Status**: 409
- **When**: Registration with existing email/username
- **Example Response**:
```json
{
  "errorCode": "USER_ALREADY_EXISTS",
  "message": "User with this email already exists",
  "httpStatus": 409,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/auth/register"
}
```

### 9. Endpoint Not Found
- **Error Code**: `ENDPOINT_NOT_FOUND`
- **HTTP Status**: 404
- **When**: Request to non-existent endpoint or wrong URL
- **Example Response**:
```json
{
  "errorCode": "ENDPOINT_NOT_FOUND",
  "message": "The requested endpoint was not found",
  "httpStatus": 404,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/auth/signins"
}
```

## Implementation Details

### Custom Exception Classes
The following custom exception classes have been created:
- `AuthException` - Base class for all authentication exceptions
- `InvalidCredentialsException` - For wrong credentials
- `InvalidTokenException` - For invalid tokens
- `TokenExpiredException` - For expired tokens
- `UserNotFoundException` - For missing users
- `UnauthorizedException` - For general auth failures
- `ForbiddenException` - For permission issues
- `EndpointNotFoundException` - For non-existent endpoints

### Global Exception Handler
The `GlobalExceptionHandler` class automatically catches authentication exceptions and converts them to appropriate HTTP responses.

### JWT Authentication Filter
The `JwtAuthenticationFilter` now throws specific exceptions based on the type of token validation failure.

### JWT Token Provider
The `JwtTokenProvider` throws specific exceptions when authentication fails due to user or token issues.

## Client-Side Handling

Clients can now handle different authentication errors appropriately:

```javascript
fetch('/api/protected-endpoint', {
  headers: {
    'Authorization': 'Bearer ' + token
  }
})
.then(response => response.json())
.then(data => {
  if (response.ok) {
    // Handle success
  } else {
    switch (data.errorCode) {
      case 'TOKEN_EXPIRED':
        // Redirect to login or refresh token
        break;
      case 'INVALID_TOKEN':
        // Clear stored token and redirect to login
        break;
      case 'FORBIDDEN':
        // Show access denied message
        break;
      case 'UNAUTHORIZED':
        // Redirect to login
        break;
      case 'ENDPOINT_NOT_FOUND':
        // Show 404 error or redirect to home
        break;
      default:
        // Handle other errors
    }
  }
});
```

## Benefits

1. **Clear Error Identification**: Each error type has a unique code
2. **Consistent Response Format**: All errors follow the same structure
3. **Better User Experience**: Clients can provide specific error messages
4. **Easier Debugging**: Detailed error messages help identify issues
5. **Proper HTTP Status Codes**: Each error type returns appropriate status codes

## Error Codes Reference

All error codes are defined in the `ErrorCodes` class for consistency:
- `INVALID_CREDENTIALS`
- `INVALID_TOKEN`
- `TOKEN_EXPIRED`
- `TOKEN_MALFORMED`
- `TOKEN_MISSING`
- `UNAUTHORIZED`
- `FORBIDDEN`
- `USER_NOT_FOUND`
- `USER_ALREADY_EXISTS`
- `ENDPOINT_NOT_FOUND`
- `VALIDATION_ERROR`
- `INTERNAL_SERVER_ERROR`

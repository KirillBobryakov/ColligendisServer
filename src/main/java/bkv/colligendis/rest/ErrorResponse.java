package bkv.colligendis.rest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String message;
    private int httpStatus;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse(String errorCode, String message, int httpStatus) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String errorCode, String message, int httpStatus, String path) {
        this(errorCode, message, httpStatus);
        this.path = path;
    }
}

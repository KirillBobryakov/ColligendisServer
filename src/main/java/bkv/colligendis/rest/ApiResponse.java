package bkv.colligendis.rest;

import lombok.Data;

@Data
public class ApiResponse<T> {

    public enum Status {
        SUCCESS,
        ERROR
    }

    private T data;
    private String message;
    private Status status;

    public ApiResponse(T data, String message, Status status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

}

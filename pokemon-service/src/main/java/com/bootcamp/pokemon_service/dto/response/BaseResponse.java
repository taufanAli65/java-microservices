package com.bootcamp.pokemon_service.dto.response;

import java.util.UUID;

public class BaseResponse<T> {
    private UUID reqId = UUID.randomUUID();
    private final Boolean status;
    private final String message;
    private final T data;

    public BaseResponse(UUID reqId, Boolean status, String message, T data) {
        this.reqId = reqId;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(Boolean status, String message, T data) {
        this(UUID.randomUUID(), status, message, data);
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, "Success", data);
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(true, message, data);
    }

    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(false, message, null);
    }

    public static <T> BaseResponse<T> error(String message, T data) {
        return new BaseResponse<>(false, message, data);
    }

    public UUID getReqId() {
        return reqId;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}

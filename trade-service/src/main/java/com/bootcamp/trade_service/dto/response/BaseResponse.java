package com.bootcamp.trade_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private UUID reqId = UUID.randomUUID();
    private Boolean status;
    private String message;
    private T data;

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
}

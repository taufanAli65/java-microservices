package com.bootcamp.trade_service.exception;

import com.bootcamp.trade_service.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalAdviceException {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadRequest(BadRequestException exception) {
    BaseResponse<Object> response = BaseResponse.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleDataNotFound(DataNotFoundException exception) {
    BaseResponse<Object> response = BaseResponse.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<String> errorMessages = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
            errorMessages.add(error.getDefaultMessage()));
        BaseResponse<Object> response = BaseResponse.error("Validation Error", errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}

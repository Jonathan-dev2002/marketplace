package com.jo.marketplace.exception;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.constant.StatusCodeEnums;
import com.jo.marketplace.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static com.jo.marketplace.constant.StatusCodeEnums.*;

@Slf4j
@RestControllerAdvice // ประกาศให้ Spring Boot รู้ว่านี่คือตัวดักจับ Error ระดับ Global
public class GlobalExceptionHandler {

    // ดักจับ Error ที่เราตั้งใจโยนเอง (Business Logic Error)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleBusinessException(BusinessException ex) {
        log.warn("Business Exception: Code={}, Message={}", ex.getCode(), ex.getMessage());
        StatusCodeEnums status = StatusCodeEnums.fromCode(ex.getCode());
        return ResponseUtil.error(status, ex.getMessage());
    }

    // ดักจับ Error จากการทำ Validation (@Valid, @NotBlank, ฯลฯ)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        log.warn("Validation Error: {}", errorMessage);

        return ResponseUtil.error(BAD_REQUEST_400, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleAllUncaughtException(Exception ex) {
        log.error("Unknown Server Error: ", ex);
        return ResponseUtil.error(SERVER_ERROR_500);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("API Path not found: {}", ex.getResourcePath());
        return ResponseUtil.error(API_NOT_FOUND_404);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Object>> handleMissingRequestBody(HttpMessageNotReadableException ex) {
        log.warn("Request Body is missing or unreadable: {}", ex.getMessage());
        return ResponseUtil.error(MISSING_REQUEST_BODY_400);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BaseResponse<Object>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.warn("Unsupported Media Type: {}", ex.getMessage());
        return ResponseUtil.error(UNSUPPORTED_MEDIA_TYPE_400);
    }
}
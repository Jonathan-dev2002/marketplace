package com.jo.marketplace.exception;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.constant.StatusCodeEnums;
import com.jo.marketplace.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice // ⚡ ประกาศให้ Spring Boot รู้ว่านี่คือตัวดักจับ Error ระดับ Global
public class GlobalExceptionHandler {

    // 🎯 1. ดักจับ Error ที่เราตั้งใจโยนเอง (Business Logic Error)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleBusinessException(BusinessException ex) {
        log.warn("Business Exception: Code={}, Message={}", ex.getCode(), ex.getMessage());
        // แปลงรหัส Code String กลับเป็น Enum เพื่อหา HttpStatus
        StatusCodeEnums status = StatusCodeEnums.fromCode(ex.getCode());
        return ResponseUtil.error(status, ex.getMessage());
    }

    // 🎯 2. ดักจับ Error จากการทำ Validation (@Valid, @NotBlank, ฯลฯ)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // ดึงเอา Field ที่มีปัญหามาต่อ String รวมกันให้สวยงาม
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        log.warn("Validation Error: {}", errorMessage);

        return ResponseUtil.error(StatusCodeEnums.BAD_REQUEST_400, errorMessage);
    }

    // 🎯 3. ดักจับ Error ที่เราไม่ได้คาดคิด (System Crash / NullPointerException)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleAllUncaughtException(Exception ex) {
        // 🛡️ พิมพ์ Stack Trace ลง Console/Log file เพื่อให้ Dev ตามแก้บั๊กได้
        log.error("Unknown Server Error: ", ex);

        // 🛡️ แต่ตอบกลับหน้าบ้านแค่ "ระบบขัดข้อง" เพื่อซ่อนโครงสร้างภายในของระบบ (Security)
        return ResponseUtil.error(StatusCodeEnums.SERVER_ERROR_500);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("API Path not found: {}", ex.getResourcePath());

        // เราใช้ NOT_FOUND_404 ที่เราสร้างไว้แล้วใน StatusCodeEnums ได้เลย
        return ResponseUtil.error(
                StatusCodeEnums.NOT_FOUND_404,
                "ไม่พบ API หรือเส้นทางที่คุณเรียกใช้งาน (กรุณาตรวจสอบ URL อีกครั้ง)"
        );
    }
}
package com.jo.marketplace.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StatusCodeEnums {

    // --- Success Codes (2xx) ---
    SUCCESS_200("2000", HttpStatus.OK, "ทำรายการสำเร็จ", "Successful."),
    CREATED_201("2001", HttpStatus.CREATED, "สร้างข้อมูลสำเร็จ", "Created successfully."),
    SHOP_CREATED_2001("2001", HttpStatus.CREATED, "เปิดร้านค้าสำเร็จ! ตอนนี้คุณได้รับการแต่งตั้งเป็น SELLER ประจำร้านแล้ว", "Shop created successfully. You are now assigned as a SELLER."),

    // --- Client Error Codes (4xx) ---
    BAD_REQUEST_400("4000", HttpStatus.BAD_REQUEST, "ข้อมูลไม่ถูกต้อง", "Invalid request data."),
    UNAUTHORIZED_401("4001", HttpStatus.UNAUTHORIZED, "ไม่ได้รับสิทธิ์เข้าถึง", "Unauthorized access."),
    NOT_FOUND_404("4004", HttpStatus.NOT_FOUND, "ไม่พบข้อมูล", "Data not found."),
    DUPLICATE_409("4009", HttpStatus.CONFLICT, "ข้อมูลซ้ำซ้อน", "Data is duplicate."),
    FORBIDDEN_403("4003", HttpStatus.FORBIDDEN, "ไม่มีสิทธิ์เข้าถึงข้อมูลส่วนนี้", "Access Denied."),

    ROLE_NOT_FOUND_404("4004", HttpStatus.NOT_FOUND, "ไม่พบข้อมูล Role ในระบบ", "Role not found."),
    INVALID_CREDENTIALS_401("4001", HttpStatus.UNAUTHORIZED, "ชื่อผู้ใช้งานหรือรหัสผ่านไม่ถูกต้อง", "Invalid username or password."),
    SHOP_NAME_DUPLICATE_400("4000", HttpStatus.BAD_REQUEST, "ชื่อร้านค้านี้ถูกใช้งานแล้ว กรุณาใช้ชื่ออื่น", "This shop name is already in use. Please choose another."),

    API_NOT_FOUND_404("4004", HttpStatus.NOT_FOUND, "ไม่พบ API หรือเส้นทางที่คุณเรียกใช้งาน (กรุณาตรวจสอบ URL อีกครั้ง)", "API path not found. Please check the URL."),
    MISSING_REQUEST_BODY_400("4000", HttpStatus.BAD_REQUEST, "กรุณาส่งข้อมูล Request Body ในรูปแบบ JSON ให้ครบถ้วน", "Required request body is missing or unreadable."),
    UNSUPPORTED_MEDIA_TYPE_400("4000", HttpStatus.BAD_REQUEST, "ระบบรองรับเฉพาะข้อมูลรูปแบบ application/json เท่านั้น", "Unsupported Media Type. Only application/json is supported."),

    // --- Server Error Codes (5xx) ---
    SERVER_ERROR_500("5000", HttpStatus.INTERNAL_SERVER_ERROR, "ระบบขัดข้อง", "Internal server error.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String descriptionTH;
    private final String descriptionEN;

    StatusCodeEnums(String code, HttpStatus httpStatus, String descriptionTH, String descriptionEN) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.descriptionTH = descriptionTH;
        this.descriptionEN = descriptionEN;
    }

    public static StatusCodeEnums fromCode(String code) {
        for (StatusCodeEnums status : StatusCodeEnums.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return SERVER_ERROR_500;
    }
}
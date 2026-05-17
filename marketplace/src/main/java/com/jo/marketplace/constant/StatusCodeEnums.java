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
    USER_NOT_FOUND_404("4004", HttpStatus.NOT_FOUND, "ไม่พบข้อมูลผู้ใช้งานในระบบ", "User not found."),
    SHOP_NOT_FOUND_404("4004", HttpStatus.NOT_FOUND, "ไม่พบข้อมูลร้านค้าในระบบ", "Shop not found."),
    INVALID_CREDENTIALS_401("4001", HttpStatus.UNAUTHORIZED, "ชื่อผู้ใช้งานหรือรหัสผ่านไม่ถูกต้อง", "Invalid username or password."),
    TOKEN_REVOKED_401("4001", HttpStatus.UNAUTHORIZED, "Token นี้ถูกยกเลิกการใช้งานแล้ว", "Token has been revoked."),
    ACCOUNT_DISABLED_403("4003", HttpStatus.FORBIDDEN, "บัญชีผู้ใช้งานนี้ไม่พร้อมใช้งาน", "User account is not active."),
    LOGOUT_SUCCESS_200("2000", HttpStatus.OK, "ออกจากระบบสำเร็จ", "Logged out successfully."),
    PASSWORD_CHANGED_200("2000", HttpStatus.OK, "เปลี่ยนรหัสผ่านสำเร็จ", "Password changed successfully."),
    CURRENT_PASSWORD_INVALID_400("4000", HttpStatus.BAD_REQUEST, "รหัสผ่านปัจจุบันไม่ถูกต้อง", "Current password is invalid."),
    REFRESH_TOKEN_INVALID_401("4001", HttpStatus.UNAUTHORIZED, "Refresh token ไม่ถูกต้องหรือหมดอายุ", "Refresh token is invalid or expired."),
    USER_PROFILE_UPDATED_200("2000", HttpStatus.OK, "แก้ไขข้อมูลผู้ใช้งานสำเร็จ", "User profile updated successfully."),
    ADDRESS_CREATED_201("2001", HttpStatus.CREATED, "เพิ่มที่อยู่สำเร็จ", "Address created successfully."),
    ADDRESS_UPDATED_200("2000", HttpStatus.OK, "แก้ไขที่อยู่สำเร็จ", "Address updated successfully."),
    ADDRESS_DELETED_200("2000", HttpStatus.OK, "ลบที่อยู่สำเร็จ", "Address deleted successfully."),
    ADDRESS_DEFAULT_UPDATED_200("2000", HttpStatus.OK, "ตั้งค่าที่อยู่เริ่มต้นสำเร็จ", "Default address updated successfully."),
    ADDRESS_NOT_FOUND_404("4004", HttpStatus.NOT_FOUND, "ไม่พบข้อมูลที่อยู่", "Address not found."),
    USER_DEACTIVATED_200("2000", HttpStatus.OK, "ปิดบัญชีผู้ใช้งานสำเร็จ", "User account deactivated successfully."),
    SHOP_NAME_DUPLICATE_400("4000", HttpStatus.BAD_REQUEST, "ชื่อร้านค้านี้ถูกใช้งานแล้ว กรุณาใช้ชื่ออื่น", "This shop name is already in use. Please choose another."),
    SHOP_UPDATED_200("2000", HttpStatus.OK, "แก้ไขข้อมูลร้านค้าสำเร็จ", "Shop updated successfully."),
    SHOP_DELETED_200("2000", HttpStatus.OK, "ลบร้านค้าสำเร็จ", "Shop deleted successfully."),
    SHOP_STATUS_UPDATED_200("2000", HttpStatus.OK, "แก้ไขสถานะร้านค้าสำเร็จ", "Shop status updated successfully."),
    SHOP_SLUG_UPDATED_200("2000", HttpStatus.OK, "แก้ไข slug ร้านค้าสำเร็จ", "Shop slug updated successfully."),
    SHOP_EMPLOYEE_ASSIGNED_200("2000", HttpStatus.OK, "เพิ่มพนักงานเข้าร้านสำเร็จ", "Shop employee assigned successfully."),
    SHOP_EMPLOYEE_REMOVED_200("2000", HttpStatus.OK, "ลบพนักงานออกจากร้านสำเร็จ", "Shop employee removed successfully."),
    SHOP_EMPLOYEE_ROLE_UPDATED_200("2000", HttpStatus.OK, "แก้ไข Role พนักงานสำเร็จ", "Shop employee role updated successfully."),
    SHOP_CHAT_ROOM_CREATED_201("2001", HttpStatus.CREATED, "สร้างห้องแชทสำเร็จ", "Shop chat room created successfully."),
    SHOP_CHAT_ROOM_UPDATED_200("2000", HttpStatus.OK, "แก้ไขห้องแชทสำเร็จ", "Shop chat room updated successfully."),
    SHOP_CHAT_ROOM_DELETED_200("2000", HttpStatus.OK, "ลบห้องแชทสำเร็จ", "Shop chat room deleted successfully."),
    SHOP_CHAT_MESSAGE_SENT_200("2000", HttpStatus.OK, "ส่งข้อความสำเร็จ", "Shop chat message sent successfully."),
    SHOP_CHAT_ROOM_NOT_FOUND_404("4004", HttpStatus.NOT_FOUND, "ไม่พบห้องแชท", "Shop chat room not found."),
    SHOP_CHAT_MESSAGE_INVALID_400("4000", HttpStatus.BAD_REQUEST, "ข้อความแชทไม่ถูกต้อง", "Shop chat message is invalid."),
    SHOP_ACCESS_DENIED_403("4003", HttpStatus.FORBIDDEN, "คุณไม่มีสิทธิ์จัดการร้านค้านี้", "You do not have permission to manage this shop."),
    SHOP_EMPLOYEE_DUPLICATE_409("4009", HttpStatus.CONFLICT, "ผู้ใช้งานนี้เป็นพนักงานของร้านอยู่แล้ว", "This user is already assigned to the shop."),
    SHOP_EMPLOYEE_NOT_FOUND_404("4004", HttpStatus.NOT_FOUND, "ไม่พบพนักงานในร้านนี้", "Shop employee not found."),
    SHOP_OWNER_OPERATION_INVALID_400("4000", HttpStatus.BAD_REQUEST, "ไม่สามารถทำรายการนี้กับเจ้าของร้านได้", "This operation cannot be applied to the shop owner."),
    SHOP_ROLE_INVALID_400("4000", HttpStatus.BAD_REQUEST, "Role นี้ไม่สามารถใช้กับร้านค้านี้ได้", "This role cannot be assigned to the shop."),
    ROLE_DUPLICATE_409("4009", HttpStatus.CONFLICT, "ชื่อ Role นี้ถูกใช้งานแล้วในร้านนี้", "This role name is already used in this shop."),
    ROLE_SYSTEM_MODIFY_INVALID_400("4000", HttpStatus.BAD_REQUEST, "ไม่สามารถแก้ไขหรือลบ System Role ได้", "System roles cannot be modified or deleted."),
    ROLE_IN_USE_409("4009", HttpStatus.CONFLICT, "Role นี้ยังมีผู้ใช้งานอยู่ ไม่สามารถลบได้", "This role is still assigned to users."),
    PERMISSION_NOT_FOUND_404("4004", HttpStatus.NOT_FOUND, "ไม่พบ Permission ในระบบ", "Permission not found."),
    SHOP_SLUG_DUPLICATE_409("4009", HttpStatus.CONFLICT, "slug ร้านค้านี้ถูกใช้งานแล้ว", "This shop slug is already in use."),
    SHOP_SLUG_INVALID_400("4000", HttpStatus.BAD_REQUEST, "slug ร้านค้าไม่ถูกต้อง", "Shop slug is invalid."),

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

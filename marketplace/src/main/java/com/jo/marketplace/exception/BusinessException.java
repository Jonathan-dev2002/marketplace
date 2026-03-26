package com.jo.marketplace.exception;

import com.jo.marketplace.constant.StatusCodeEnums;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final String message;

    // 🛡️ Constructor แบบส่ง Enum เข้ามาตรงๆ (แนะนำให้ใช้อันนี้ สะอาดที่สุด)
    public BusinessException(StatusCodeEnums status) {
        super(status.getDescriptionEN());
        this.code = status.getCode();
        this.message = status.getDescriptionTH(); // หรือจะสลับเป็น EN ตาม Requirement ของทีม
    }

    // 🛡️ Constructor แบบ Custom ข้อความเอง
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    public BusinessException(StatusCodeEnums status , String customMessage) {
        super(customMessage);
        this.code = status.getCode();
        this.message = customMessage;
    }
}
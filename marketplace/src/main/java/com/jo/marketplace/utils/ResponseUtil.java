package com.jo.marketplace.utils;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.constant.StatusCodeEnums;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    // คืนค่าแบบสำเร็จ (มี Data)
    public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
        return ResponseEntity.ok(buildResponse(StatusCodeEnums.SUCCESS_200, data));
    }

    // คืนค่าแบบสำเร็จ (กำหนด Status เอง เช่น 201 Created)
    public static <T> ResponseEntity<BaseResponse<T>> success(StatusCodeEnums status, T data) {
        return ResponseEntity.status(status.getHttpStatus()).body(buildResponse(status, data));
    }

    // คืนค่าแบบ Error (ไม่มี Data)
    public static <T> ResponseEntity<BaseResponse<T>> error(StatusCodeEnums status) {
        return ResponseEntity.status(status.getHttpStatus()).body(buildResponse(status, null));
    }

    // คืนค่าแบบ Error พร้อมข้อความ Custom (มีประโยชน์ตอนดัก Exception)
    public static <T> ResponseEntity<BaseResponse<T>> error(StatusCodeEnums status, String customMessage) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(new BaseResponse.Status()
                .setCode(status.getCode())
                .setDescription(customMessage));
        return ResponseEntity.status(status.getHttpStatus()).body(response);
    }

    // ฟังก์ชันช่วยประกอบร่าง Response
    private static <T> BaseResponse<T> buildResponse(StatusCodeEnums status, T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(new BaseResponse.Status()
                .setCode(status.getCode())
                .setDescription(status.getDescriptionEN())); // ตั้ง Default ส่งข้อความภาษาอังกฤษกลับไป
        response.setData(data);
        return response;
    }
}
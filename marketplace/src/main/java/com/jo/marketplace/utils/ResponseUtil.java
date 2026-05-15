package com.jo.marketplace.utils;

import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.constant.StatusCodeEnums;
import org.springframework.http.ResponseEntity;

public final class ResponseUtil {

    private ResponseUtil() {
    }

    public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
        return ResponseEntity.ok(buildResponse(StatusCodeEnums.SUCCESS_200, data));
    }

    public static <T> ResponseEntity<BaseResponse<T>> success(StatusCodeEnums status, T data) {
        return ResponseEntity.status(status.getHttpStatus()).body(buildResponse(status, data));
    }

    public static <T> ResponseEntity<BaseResponse<T>> error(StatusCodeEnums status) {
        return ResponseEntity.status(status.getHttpStatus()).body(buildResponse(status, null));
    }

    public static <T> ResponseEntity<BaseResponse<T>> error(StatusCodeEnums status, String customMessage) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(new BaseResponse.Status()
                .setCode(status.getCode())
                .setDescription(customMessage));
        return ResponseEntity.status(status.getHttpStatus()).body(response);
    }

    private static <T> BaseResponse<T> buildResponse(StatusCodeEnums status, T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus(new BaseResponse.Status()
                .setCode(status.getCode())
                .setDescription(status.getDescriptionEN()));
        response.setData(data);
        return response;
    }
}

package com.jo.marketplace.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo.marketplace.common.BaseResponse;
import com.jo.marketplace.constant.StatusCodeEnums;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // 1. ตั้งค่า Header ว่าจะตอบกลับเป็น JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // 2. ประกอบร่าง BaseResponse ของเรา
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        baseResponse.setStatus(new BaseResponse.Status()
                .setCode(StatusCodeEnums.UNAUTHORIZED_401.getCode())
                .setDescription("กรุณาเข้าสู่ระบบ (Token is missing or invalid)"));

        // 3. แปลง Java Object เป็น JSON แล้วเขียนส่งกลับไป
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), baseResponse);
    }
}